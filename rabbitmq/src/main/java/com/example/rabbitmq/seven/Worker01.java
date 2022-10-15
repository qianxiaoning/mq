package com.example.rabbitmq.seven;

import com.example.rabbitmq.two.RabbitMqUtils;
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
    // 交换机名称
    private final static String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws Exception {
        // 创建一个连接工厂
        Channel channel = RabbitMqUtils.getChannel();
        // 声明交换机
        /**
         * 1.交换机名称
         * 2.交换机类型
         */
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        // 声明一个临时队列，当消费者断开与队列连接时，队列会自动删除
        String queueName = channel.queueDeclare().getQueue();
        // 交换机与队列绑定
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        System.out.println("等待接收消息，把接受消息打印");
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("Worker01接受消息" + new String(message.getBody(), "UTF-8"));
        };
        channel.basicConsume(queueName, true, deliverCallback, (CancelCallback) null);
    }
}
