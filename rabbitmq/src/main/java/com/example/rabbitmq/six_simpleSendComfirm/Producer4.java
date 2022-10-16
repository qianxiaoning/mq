package com.example.rabbitmq.six_simpleSendComfirm;

import com.example.rabbitmq.two_workQueue.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

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
public class Producer4 {
    // 批量发消息个数
    private final static Integer MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws Exception {
        // 处理异步未确认消息
        async();
    }

    // 异步确认
    private static void  async() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, false, false, false, null);
        // 开启发布确认
        channel.confirmSelect();
        /**
         * 线程安全效率高的map，适用于高并发情况
         * 1.将序号与消息关联
         * 2.可批量删除条目
         * 3.线程安全
         */
        ConcurrentSkipListMap<Long, String> map = new ConcurrentSkipListMap<>();
        // 开始时间
        long start = System.currentTimeMillis();

        // 消息监听器，监听消息成功失败
        // 消息确认成功回调
        /**
         * 1.消息标识
         * 2.是否批量确认
         */
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
            if(multiple) {
                // 批量
                // 2.删除确认消息
                ConcurrentNavigableMap<Long, String> confirmMap = map.headMap(deliveryTag);
                confirmMap.clear();
            } else {
                // 不是批量，只删当前消息
                map.remove(deliveryTag);
            }
            System.out.println("确认的消息" + deliveryTag);
        };
        // 消息确认失败回调
        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
            // 3.打印未确认消息
            String message = map.get(deliveryTag);
            System.out.println("未确认的消息" + message);
            System.out.println("未确认的消息标记" + deliveryTag);
        };
        channel.addConfirmListener(ackCallback, nackCallback);// 异步的

        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
            // 1.记录所有消息
            map.put(channel.getNextPublishSeqNo(), message);
        }
        // 结束时间
        long end = System.currentTimeMillis();
        System.out.println("耗时" + (end - start) + "ms");
    }
}
