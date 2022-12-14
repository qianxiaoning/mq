package com.example.rabbitmq.fourteen_priorityQueue;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 版权：(C) 版权所有 2000-2021
 * <简述>
 * <详细描述> Producer
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
public class Producer {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        // 创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.137.101");
        factory.setUsername("admin");
        factory.setPassword("123");
        // 创建连接
        Connection connection = factory.newConnection();
        // 获取信道
        Channel channel = connection.createChannel();
        /**
         * 生成一个队列
         * 1.队列名称
         * 2.队列里面的消息是否持久化 默认消息存储在内存中
         * 3.该队列是否只供一个消费者进行消费 是否独占 true 独占
         * 4.是否自动删除 最后一个消费者端开连接以后 该队列是否自动删除 true 自动删除
         * 5.其他参数
         */
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-max-priority", 10); // 设置优先级范围0-10
        channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);
        // channel 实现了自动 close 接口 自动关闭 不需要显示关闭
        // 发消息
        /**
         * 发送一个消息
         * 1.发送到那个交换机
         * 2.路由的 key 是哪个
         * 3.其他的参数信息
         * 4.发送消息的消息体
         */
        for (int i = 0; i < 11; i++) {
            String message = "info" + i;
            if (i == 5) {
                // 给info5消息设置优先级
                AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().priority(5).build();
                channel.basicPublish("", QUEUE_NAME, properties, message.getBytes());
            } else {
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            }
        }
        System.out.println("消息发送完毕");
    }
}
