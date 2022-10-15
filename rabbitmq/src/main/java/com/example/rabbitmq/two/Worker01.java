package com.example.rabbitmq.two;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * 版权：(C) 版权所有 2000-2021
 * <简述>
 * <详细描述> Consumer
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
public class Worker01 {
    // 队列名称
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        // 接受消息
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println(message);
            System.out.println(new String(message.getBody()));
        };
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println(consumerTag + "消息消费中断");
        };
        System.out.println("c1等待接受消息");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
