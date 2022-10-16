package com.example.rabbitmq.eight_directExchange;

import com.example.rabbitmq.two_workQueue.RabbitMqUtils;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

/**
 * 版权：(C) 版权所有 2000-2021
 * <简述> 直接交换机
 * <详细描述> Consumer
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
public class Emiter01 {
    // 交换机名称
    private final static String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws Exception {
        // 获取信道
        Channel channel = RabbitMqUtils.getChannel();
        // 控制台输入
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish(EXCHANGE_NAME, "warning", null, message.getBytes("UTF-8"));
            System.out.println("发送消息：" + message);
        }
    }
}
