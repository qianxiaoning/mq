package com.example.rabbitmq.six;

import com.example.rabbitmq.two.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.util.UUID;

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
    // 批量发消息个数
    private final static Integer MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws Exception {
        // 单个发布确认，3961ms
//        single();
        // 批量发布确认，148ms
//        batch();
        // 异步发布确认，31ms
        async();
    }

    // 单个确认
    private static void single() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, false, false, false, null);
        // 开启发布确认
        channel.confirmSelect();
        // 开始时间
        long start = System.currentTimeMillis();
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
            // 发布确认
            boolean flag = channel.waitForConfirms();
            if (flag) {
                System.out.println("消息发送成功");
            }
        }
        // 结束时间
        long end = System.currentTimeMillis();
        System.out.println("耗时" + (end - start) + "ms");
    }

    // 批量确认
    private static void batch() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, false, false, false, null);
        // 开启发布确认
        channel.confirmSelect();
        // 开始时间
        long start = System.currentTimeMillis();
        // 批量发布长度
        Integer batchSize = 100;
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
            // 发布确认
            if ((i+1) % batchSize == 0) {
                boolean flag = channel.waitForConfirms();
                if (flag) {
                    System.out.println("消息发送成功");
                }
            }
        }
        // 结束时间
        long end = System.currentTimeMillis();
        System.out.println("耗时" + (end - start) + "ms");
    }

    // 异步确认
    private static void  async() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, false, false, false, null);
        // 开启发布确认
        channel.confirmSelect();
        // 开始时间
        long start = System.currentTimeMillis();

        // 消息监听器，监听消息成功失败
        // 消息确认成功回调
        /**
         * 1.消息标识
         * 2.是否批量确认
         */
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
            System.out.println("确认的消息" + deliveryTag);
        };
        // 消息确认失败回调
        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
            System.out.println("未确认的消息" + deliveryTag);
        };
        channel.addConfirmListener(ackCallback, nackCallback);// 异步的

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
        }
        // 结束时间
        long end = System.currentTimeMillis();
        System.out.println("耗时" + (end - start) + "ms");
    }
}
