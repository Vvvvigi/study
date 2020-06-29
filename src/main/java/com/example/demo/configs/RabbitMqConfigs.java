package com.example.demo.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RabbitMqConfigs {
    private static final Logger log = LoggerFactory.getLogger(RabbitMqConfigs.class);

    @Autowired
    private Environment env;

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Autowired
    private SimpleRabbitListenerContainerFactoryConfigurer factoryConfigurer;

    /**
     * 单一
     * @return
     */
    @Bean(name = "singleListenerContainer")
    public SimpleRabbitListenerContainerFactory listenerContainer(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return factory;
    }

    @Bean(name="multiListenerContainer")
    public SimpleRabbitListenerContainerFactory multiListenerContainer(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factoryConfigurer.configure(factory,connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.NONE);
        factory.setConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.simple.concurrency",int.class));
        factory.setMaxConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.simple.max-concurrency",int.class));
        factory.setPrefetchCount(env.getProperty("spring.rabbitmq.listener.simple.prefetch",int.class));
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(){
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((CorrelationData correlationData, boolean b, String s)->
                log.info("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,b,s)
        );
        rabbitTemplate.setReturnCallback((Message message, int i, String s, String s1, String s2)->
                log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",s1,s2,i,s,message)
        );
        return rabbitTemplate;
    }


    @Bean(name="userOrderQueue")
    public Queue userOrderQueue(){return new Queue(env.getProperty("user.order.queue.name"),true);}

    @Bean
    public TopicExchange userOrderExchange(){
        return new TopicExchange(env.getProperty("user.order.exchange.name"),true,false);
    }
    @Bean
    public Binding userOrderBinding(){
        return  BindingBuilder.bind(userOrderQueue()).to(userOrderExchange()).with(env.getProperty("user.order.routing.key.name"));
    }

    @Autowired
    private  UserOrderListener userOrderListener;


    @Bean
    public SimpleMessageListenerContainer listenerContainerUserOrder(@Qualifier("userOrderQueue") Queue userOrderQueue){
        SimpleMessageListenerContainer container  = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setMessagePropertiesConverter(new DefaultMessagePropertiesConverter());

        container.setConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.simple.concurrency",Integer.class));
        container.setMaxConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.simple.max-concurrency",Integer.class));
        container.setPrefetchCount(env.getProperty("spring.rabbitmq.listener.simple.prefetch",Integer.class));

        container.setQueues(userOrderQueue);
        container.setMessageListener(userOrderListener);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        return  container;
    }
}
