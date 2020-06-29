package com.example.demo.configs;

import com.example.demo.redis.RedisUtil;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("userOrderListener")
public class UserOrderListener implements ChannelAwareMessageListener {
    private static final Logger log = LoggerFactory.getLogger(UserOrderListener.class);

    @Resource
    private RedisUtil redis;


    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            byte[] body = message.getBody();
            String mobile = new String(body,"UTF-8");
            if(AckOrder()){
                channel.basicAck(tag,true);
                log.info("抢单成功,手机号{}",mobile);
            }else{
                channel.basicAck(tag,false);
                log.info("抢单失败,手机号{}",mobile);
            }
        }catch (Exception e){
            log.error("抢单异常:",e.fillInStackTrace());
            channel.basicAck(tag,false);
        }
    }

    /**
     * redis模拟下减库存
     * @return
     */
    public synchronized boolean  AckOrder(){
        if(redis.hasKey("order") && (int)redis.get("order") >0){
            int order = (int)redis.get("order");
            order -= 1;
            redis.set("order",order);
            return true;
        }
        return false;
    }

}
