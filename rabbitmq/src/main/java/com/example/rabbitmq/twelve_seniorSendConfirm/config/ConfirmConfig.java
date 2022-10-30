package com.example.rabbitmq.twelve_seniorSendConfirm.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <简述> 发布确认-高级
 * <详细描述> confirmConfig
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */

@Configuration
public class ConfirmConfig {
    // 确认交换机
    public static final String CONFIRM_EXCHANGE_NAME = "confirm_exchange";
    // 确认交换机2（会转发备份交换机的那个）
    public static final String CONFIRM_EXCHANGE_NAME2 = "confirm_exchange2";
    // 队列
    public static final String CONFIRM_QUEUE_NAME = "confirm_queue";
    // routingKey
    public static final String CONFIRM_ROUTING_KEY = "key1";

    // 声明交换机
    @Bean
    public DirectExchange confirmExchange(){
        return new DirectExchange(CONFIRM_EXCHANGE_NAME);
    }
    @Bean
    public Queue confirmQueue(){
//        return new Queue(CONFIRM_EXCHANGE_NAME);
        // 或用构建方法
        return QueueBuilder.durable(CONFIRM_QUEUE_NAME).build();
    }
    @Bean
    public Binding QueueBindExchange(Queue confirmQueue, DirectExchange confirmExchange){
        return BindingBuilder.bind(confirmQueue).to(confirmExchange).with(CONFIRM_ROUTING_KEY);
    }

}
