package com.example.rabbitmq.thirteen_backupExchange.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <简述> 备份交换机
 * <详细描述> confirmConfig
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */

@Configuration
public class ConfirmConfig1 {
    // 交换机
    public static final String CONFIRM_EXCHANGE_NAME2 = "confirm_exchange2";
    // routingKey
    public static final String CONFIRM_ROUTING_KEY = "key1";
    // 备份交换机
    public static final String BACKUP_EXCHANGE_NAME = "backup_exchange";
    // 备份队列
    public static final String BACKUP_QUEUE_NAME = "backup_queue";
    // 报警队列
    public static final String WARNING_QUEUE_NAME = "warning_queue";

    // 声明交换机
    @Bean
    public DirectExchange confirmExchange1(){
        // 确认交换机通就给确认队列，确认交换机不通就转发给备份交换机
        // 交换机的构建
        return ExchangeBuilder.directExchange(CONFIRM_EXCHANGE_NAME2)
                .durable(true) // 持久化
                .withArgument("alternate-exchange", BACKUP_EXCHANGE_NAME) // 不通就可以选择备份交换机
                .build();
    }
    @Bean
    public Binding QueueBindExchange1(Queue confirmQueue, DirectExchange confirmExchange1){
        return BindingBuilder.bind(confirmQueue).to(confirmExchange1).with(CONFIRM_ROUTING_KEY);
    }

    // 声明备份交换机
    @Bean
    public FanoutExchange backupExchange(){
        return new FanoutExchange(BACKUP_EXCHANGE_NAME);
    }
    @Bean
    public Queue backupQueue(){
        return QueueBuilder.durable(BACKUP_QUEUE_NAME).build();
    }
    @Bean
    public Queue warningQueue(){
        return QueueBuilder.durable(WARNING_QUEUE_NAME).build();
    }
    @Bean
    public Binding backupQueueBindBackupExchange(Queue backupQueue, FanoutExchange backupExchange){
        return BindingBuilder.bind(backupQueue).to(backupExchange);
    }
    @Bean
    public Binding warningQueueBindBackupExchange(Queue warningQueue, FanoutExchange warningExchange){
        return BindingBuilder.bind(warningQueue).to(warningExchange);
    }
}
