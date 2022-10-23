package com.example.rabbitmq.twelve_seniorSendConfirm.consumer;

import com.example.rabbitmq.eleven_delayQueue.config.DelayedQueueConfig;
import com.example.rabbitmq.twelve_seniorSendConfirm.config.ConfirmConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * <简述>
 * <详细描述> DelayQueueConsumer
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
@Slf4j
@Component
public class Consumer {
    // 监听消息
    @RabbitListener(queues = ConfirmConfig.CONFIRM_QUEUE_NAME)
    public void receiveConfirmQueue(Message message){
        String msg = new String(message.getBody());
        log.info("接收到的队列confirm.queue消息：{}", msg);
    }
}
