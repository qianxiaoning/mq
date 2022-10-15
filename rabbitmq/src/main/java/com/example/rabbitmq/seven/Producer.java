package com.example.rabbitmq.seven;

import com.example.rabbitmq.two.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

/**
 * 版权：(C) 版权所有 2000-2021
 * <简述> 交换机
 * <详细描述> Producer
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
public class Producer {
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
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        // 控制台输入
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
            System.out.println("发送消息完成");
        }
    }
}
