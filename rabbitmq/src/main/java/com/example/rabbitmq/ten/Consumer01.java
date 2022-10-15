package com.example.rabbitmq.ten;

import com.example.rabbitmq.two.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * 版权：(C) 版权所有 2000-2021
 * <简述> 死信
 * <详细描述> 启动Consumer01后马上关闭，再启动消费者，等消息过了ttl时间，进入死信
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
public class Consumer01 {
    // 普通交换机名称
    private final static String NORMAL_EXCHANGE = "normal_exchange";
    // 死信交换机名称
    private final static String DEAD_EXCHANGE = "dead_exchange";
    // 普通队列名称
    private final static String NORMAL_QUEUE = "normal_queue";
    // 死信队列名称
    private final static String DEAD_QUEUE = "dead_queue";
    // 普通RoutingKey
    private final static String NORMAL_ROUTINGKEY = "zhangsan";
    // 死信RoutingKey
    private final static String DEAD_ROUTINGKEY = "lisi";

    public static void main(String[] args) throws Exception {
        // 获取信道
        Channel channel = RabbitMqUtils.getChannel();
        // 声明交换机
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
        // 声明普通队列
        Map<String, Object> arguments = new HashMap<>();
        // 过期时间ms
//        arguments.put("x-message-ttl", 10000);
        // 正常队列设置死信交换机
        arguments.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        // 设置死信RoutingKey
        arguments.put("x-dead-letter-routing-key", DEAD_ROUTINGKEY);
        channel.queueDeclare(NORMAL_QUEUE, false, false, false, arguments);
        // 声明死信队列
        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);

        // 绑定普通的交换机和普通队列
        channel.queueBind(NORMAL_QUEUE, NORMAL_EXCHANGE, NORMAL_ROUTINGKEY);
        // 绑定死信的交换机和死信队列
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, DEAD_ROUTINGKEY);
        System.out.println("等待接收消息");
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("consumer01接收的消息是:" + new String(message.getBody(), "UTF-8"));
        };
        channel.basicConsume(NORMAL_QUEUE, true, deliverCallback, (CancelCallback) null);
    }
}
