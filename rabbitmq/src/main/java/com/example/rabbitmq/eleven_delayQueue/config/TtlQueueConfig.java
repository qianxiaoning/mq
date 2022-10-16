package com.example.rabbitmq.eleven_delayQueue.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 版权：(C) 版权所有 2000-2021 上海天好电子商务股份有限公司苏州分公司
 * <简述> 延迟队列-交换机、队列等中间配置
 * <详细描述> TtlQueueConfig
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
@Configuration
public class TtlQueueConfig {
    // 普通交换机的名称
    public static final String X_EXCHANGE = "X";
    // 死信交换机的名称
    public static final String Y_DEAD_LETTER_EXCHANGE = "Y";
    // 普通队列的名称
    public static final String QUEUE_A = "QA";
    public static final String QUEUE_B = "QB";

    // routingKey
    public static final String QUEUE_A_ROUTINGKEY = "XA";
    public static final String QUEUE_B_ROUTINGKEY = "XB";
    public static final String DEAD_LETTER_ROUTINGKEY = "YD";
    // 死信队列的名称
    public static final String DEAD_LETTER_QUEUE = "QD";
    // 延迟队列优化
    public static final String QUEUE_C = "QC";
    public static final String QUEUE_C_ROUTINGKEY = "XC";

    // 注解方式声明，2交换机，3队列
    // 声明x交换机 别名
    @Bean("xExchange")
    public DirectExchange xExchange(){
        return new DirectExchange(X_EXCHANGE);
    }
    // 声明y交换机 别名
    @Bean("yExchange")
    public DirectExchange yExchange(){
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }
    // 声明普通队列，ttl为10s
    @Bean("queueA")
    public Queue queueA(){
        Map<String, Object> arguments = new HashMap<>(3);
        // 设置死信交换机
        arguments.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        // 设置死信routingKey
        arguments.put("x-dead-letter-routing-key", DEAD_LETTER_ROUTINGKEY);
        // 设置ttl ms
        arguments.put("x-message-ttl", 10000);
        return QueueBuilder.durable(QUEUE_A).withArguments(arguments).build();
    }
    @Bean("queueB")
    public Queue queueB(){
        Map<String, Object> arguments = new HashMap<>(3);
        // 设置死信交换机
        arguments.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        // 设置死信routingKey
        arguments.put("x-dead-letter-routing-key", DEAD_LETTER_ROUTINGKEY);
        // 设置ttl ms
        arguments.put("x-message-ttl", 40000);
        return QueueBuilder.durable(QUEUE_B).withArguments(arguments).build();
    }
    // 声明死信队列
    @Bean("queueD")
    public Queue queueD(){
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }
    // 3个绑定。x交换机和队列A、B绑定，y交换机和队列D绑定
    @Bean
    public Binding queueABindingX(Queue queueA, DirectExchange xExchange){
        return BindingBuilder.bind(queueA).to(xExchange).with(QUEUE_A_ROUTINGKEY);
    }
    @Bean
    public Binding queueBBindingX(Queue queueB, DirectExchange xExchange){
        return BindingBuilder.bind(queueB).to(xExchange).with(QUEUE_B_ROUTINGKEY);
    }
    @Bean
    public Binding queueDBindingY(Queue queueD, DirectExchange yExchange){
        return BindingBuilder.bind(queueD).to(yExchange).with(DEAD_LETTER_ROUTINGKEY);
    }

    // 延迟队列优化，造一个适合所有延迟时间的队列QC
    @Bean("queueC")
    public Queue queueC(){
        Map<String, Object> arguments = new HashMap<>(3);
        // 设置死信交换机
        arguments.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        // 设置死信routingKey
        arguments.put("x-dead-letter-routing-key", DEAD_LETTER_ROUTINGKEY);
        return QueueBuilder.durable(QUEUE_C).withArguments(arguments).build();
    }
    @Bean
    public Binding queueCBindingX(Queue queueC, DirectExchange xExchange){
        return BindingBuilder.bind(queueC).to(xExchange).with(QUEUE_C_ROUTINGKEY);
    }
}
