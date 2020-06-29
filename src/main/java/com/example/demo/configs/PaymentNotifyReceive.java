package com.example.demo.configs;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "hello")
public class PaymentNotifyReceive {
    @RabbitHandler
    public void process(String msg) {
        System.out.print(msg);
    }
}
