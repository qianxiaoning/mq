package com.example.rabbitmq.eleven_delayQueue.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * <简述> 延迟队列-消费者监听器
 * <详细描述> DeadLetterQueueConsumer
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
@Slf4j
@Component
public class DeadLetterQueueConsumer {

    // 普通交换机的名称
    public static final String X_EXCHANGE = "X";
    // 死信交换机的名称
    public static final String Y_DEAD_LETTER_EXCHANGE = "Y";
    // 普通队列的名称
    public static final String QUEUE_A = "QA";
    public static final String QUEUE_B = "QB";
    // routingKey
    public static final String QUEUE_A_ROUTINGKEY = "XA";
    public static final String QUEUE_B_ROUTINGKEY = "XB";
    public static final String DEAD_LETTER_ROUTINGKEY = "YD";
    // 死信队列的名称
    public static final String DEAD_LETTER_QUEUE = "QD";

    // 接收消息
    @RabbitListener(queues = DEAD_LETTER_QUEUE)
    public void receiveD(Message message, Channel channel){
        String msg = new String(message.getBody());
        log.info("当前时间：{}，收到死信队列的消息：{}", new Date().toString(), msg);
    }
}

