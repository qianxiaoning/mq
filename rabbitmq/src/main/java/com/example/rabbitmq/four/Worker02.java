package com.example.rabbitmq.four;

import com.example.rabbitmq.two.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

/**
 * 版权：(C) 版权所有 2000-2021 上海天好电子商务股份有限公司苏州分公司
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
        System.out.println("c2处理慢");
        // 接受消息
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            SleepUtils.sleep(30);
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
        // false手动应答
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, cancelCallback);
    }
}
