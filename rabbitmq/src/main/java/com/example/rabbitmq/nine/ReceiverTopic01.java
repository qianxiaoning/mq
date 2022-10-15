package com.example.rabbitmq.nine;

import com.example.rabbitmq.two.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * 版权：(C) 版权所有 2000-2021
 * <简述> 主题交换机
 * <详细描述> Consumer
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
public class ReceiverTopic01 {
    // 交换机名称
    private final static String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {
        // 获取信道
        Channel channel = RabbitMqUtils.getChannel();
        // 声明交换机
        /**
         * 1.交换机名称
         * 2.交换机类型
         */
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String queueName = "Q1";
        channel.queueDeclare(queueName, false, false, false, null);
        // 绑定
        channel.queueBind(queueName, EXCHANGE_NAME, "*.orange.*");
        System.out.println("等待接收消息");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println(new String(message.getBody(), "UTF-8"));
            System.out.println("接收队列:" + queueName + " 绑定键： " + message.getEnvelope().getRoutingKey());
        };
        channel.basicConsume(queueName, true, deliverCallback, (CancelCallback) null);
    }
}
