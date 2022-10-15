package com.example.rabbitmq.ten;

import com.example.rabbitmq.two.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * 版权：(C) 版权所有 2000-2021
 * <简述> 死信
 * <详细描述> Consumer
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
public class Consumer02 {
    // 死信队列名称
    private final static String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        // 获取信道
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("等待接收消息");
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("consumer02接收的消息是:" + new String(message.getBody(), "UTF-8"));
        };
        channel.basicConsume(DEAD_QUEUE, true, deliverCallback, (CancelCallback) null);
    }
}
