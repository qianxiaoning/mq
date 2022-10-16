package com.example.rabbitmq.eleven_delayQueue.consumer;

import com.example.rabbitmq.eleven_delayQueue.config.DelayedQueueConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 版权：(C) 版权所有 2000-2021 上海天好电子商务股份有限公司苏州分公司
 * <简述> 基于插件的延迟队列消费者
 * <详细描述> DelayQueueConsumer
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
@Slf4j
@Component
public class DelayQueueConsumer {
    // 监听消息
    @RabbitListener(queues = DelayedQueueConfig.DELAYED_QUEUE_NAME)
    public void receiveDelayQueue(Message message, Channel channel){
        String msg = new String(message.getBody());
        log.info("当前时间：{}，收到延迟队列的消息：{}", new Date().toString(), msg);
    }
}
