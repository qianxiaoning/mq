package com.example.rabbitmq.five;

import com.example.rabbitmq.four.SleepUtils;
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
public class Worker02 {
    // 队列名称
    private final static String QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        // 接受消息
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            SleepUtils.sleep(2);
            System.out.println(message);
            System.out.println(new String(message.getBody(),"UTF-8"));
            // 手动应答
            /**
             * 1.消息的标识tag
             * 2.是否批量应答
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println(consumerTag + "消息消费中断");
        };
        // 定义信道上允许的未确认消息的最大数量
        int prefetchCount = 1;
        channel.basicQos(prefetchCount);
        // false手动应答
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, cancelCallback);
    }
}
