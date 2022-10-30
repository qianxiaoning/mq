package com.example.rabbitmq.thirteen_backupExchange.consumer;

import com.example.rabbitmq.thirteen_backupExchange.config.ConfirmConfig1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * <简述> 报警消费者
 * <详细描述>
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
@Slf4j
@Component
public class WarningConsumer {
    // 接收报警消息
    @RabbitListener(queues = ConfirmConfig1.WARNING_QUEUE_NAME)
    public void receiveWarningQueue(Message message){
        String msg = new String(message.getBody());
        log.error("报警发现不可路由消息：{}", msg);
    }
}
