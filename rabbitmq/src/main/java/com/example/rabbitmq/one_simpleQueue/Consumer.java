package com.example.rabbitmq.one_simpleQueue;

import com.rabbitmq.client.*;

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
public class Consumer {
    // 队列名称
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        // 连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.30.117");
        factory.setUsername("admin");
        factory.setPassword("123");
        // 信道
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        /**
         * 消费者消费消息
         * 1.消费哪个队列
         * 2.消费成功后是否自动应答，true自动应答
         * 3.消费传达后的回调
         * 4.消费者取消消费的回调
         */
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println(message);
            System.out.println(new String(message.getBody()));
        };
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("消息消费中断");
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
