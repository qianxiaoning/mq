package com.example.rabbitmq.two;

import com.rabbitmq.client.Channel;

import java.util.Scanner;

/**
 * 版权：(C) 版权所有 2000-2021 上海天好电子商务股份有限公司苏州分公司
 * <简述>
 * <详细描述> Producer
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
public class Producer2 {
    private final static String QUEUE_NAME = "hello";

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
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 控制台输入
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("发送消息完成");
        }

    }
}
