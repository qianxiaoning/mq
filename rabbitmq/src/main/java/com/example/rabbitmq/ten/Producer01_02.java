package com.example.rabbitmq.ten;

import com.example.rabbitmq.two.RabbitMqUtils;
import com.rabbitmq.client.Channel;

/**
 * 版权：(C) 版权所有 2000-2021
 * <简述> 死信队列生产者，去掉过期时间
 * <详细描述>
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
public class Producer01_02 {
    // 普通交换机名称
    private final static String NORMAL_EXCHANGE = "normal_exchange";
    private final static String NORMAL_ROUTINGKEY = "zhangsan";

    public static void main(String[] args) throws Exception {
        // 获取信道
        Channel channel = RabbitMqUtils.getChannel();
        // 死信消息，设置TTL时间
//        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();
        for (int i = 1; i < 11; i++) {
            String message = "info" + i;
            channel.basicPublish(NORMAL_EXCHANGE, NORMAL_ROUTINGKEY, null, message.getBytes());
        }

    }
}
