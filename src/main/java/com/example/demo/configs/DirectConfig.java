package com.example.demo.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DirectConfig {
    @Bean
    public Queue queue() {
        return new Queue("hello");
    }
}
