package com.example.rabbitmq.four;

import com.example.rabbitmq.two.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;

/**
 * 版权：(C) 版权所有 2000-2021
 * <简述> 持久化
 * <详细描述> Producer
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
public class Producer3 {
    private final static String QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        // 创建一个连接工厂
        Channel channel = RabbitMqUtils.getChannel();
        /**
         * 生成一个队列
         * 1.队列名称
         * 2.队列里面的消息是否持久化 默认消息存储在内存中
         * 3.该队列是否只供一个消费者进行消费 是否独占 true 独占
         * 4.是否自动删除 最后一个消费者端开连接以后 该队列是否自动删除 true 自动删除
         * 5.其他参数
         */
        Boolean durable = true;// 持久化
        // 需要把原不持久化队列删除
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
        // 控制台输入
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            // MessageProperties.PERSISTENT_TEXT_PLAIN 消息持久化，保存到磁盘上
            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
            System.out.println("发送消息完成");
        }

    }
}
