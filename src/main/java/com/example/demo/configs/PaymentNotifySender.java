package com.example.demo.configs;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PaymentNotifySender {
    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void sender() {
        String context = "hello----" + LocalDateTime.now();
        System.out.println("send:" + context);
        //往名称为 hello 的queue中发送消息
        this.rabbitTemplate.convertAndSend("hello", context);
    }
}
