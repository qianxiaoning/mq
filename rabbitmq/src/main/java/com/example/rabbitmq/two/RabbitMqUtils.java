package com.example.rabbitmq.two;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 版权：(C) 版权所有 2000-2021 上海天好电子商务股份有限公司苏州分公司
 * <简述> 连接工厂，创建信道
 * <详细描述> RabbitMqUtils
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
public class RabbitMqUtils {
    //得到一个连接的 channel
    public static Channel getChannel() throws Exception {
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.30.117");
        factory.setUsername("admin");
        factory.setPassword("123");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        return channel;
    }
}
