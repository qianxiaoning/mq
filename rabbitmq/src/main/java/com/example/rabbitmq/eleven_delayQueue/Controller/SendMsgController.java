package com.example.rabbitmq.eleven_delayQueue.Controller;

import com.example.rabbitmq.eleven_delayQueue.config.DelayedQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * <简述> 延迟队列-发送消息
 * <详细描述> SendMsgController
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
@Slf4j
@RestController
@RequestMapping("/ttl")
public class SendMsgController {

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
    public static final String QUEUE_C_ROUTINGKEY = "XC";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 发消息
    @GetMapping("/sendMsg/{message}")
    public void sendMsg(@PathVariable String message){
        log.info("当前时间：{}，发送一条信息给两个ttl队列{}", new Date().toString(), message);
        rabbitTemplate.convertAndSend(X_EXCHANGE, QUEUE_A_ROUTINGKEY, "消息来自ttl为10s的队列：" + message);
        rabbitTemplate.convertAndSend(X_EXCHANGE, QUEUE_B_ROUTINGKEY, "消息来自ttl为40s的队列：" + message);
    }

    // 发消息，带上ttl
    @GetMapping("/sendExpirationMsg/{message}/{ttlTime}")
    public void sendMsg(@PathVariable String message, @PathVariable String ttlTime){
        log.info("当前时间：{}，发送一条时长{}毫秒信息给队列QC:{}", new Date().toString(), ttlTime, message);
        rabbitTemplate.convertAndSend(X_EXCHANGE, QUEUE_C_ROUTINGKEY, message,
                msg -> {
                    // 设置发送消息的延迟时长
                    msg.getMessageProperties().setExpiration(ttlTime);
                    return msg;
                }
            );
    }

    // 发消息，基于插件
    @GetMapping("/sendDelayMsg/{message}/{delayTime}")
    public void sendMsg(@PathVariable String message, @PathVariable Integer delayTime){
        log.info("当前时间：{}，发送一条时长{}毫秒信息给延迟队列delayed.queue:{}", new Date().toString(), delayTime, message);
        rabbitTemplate.convertAndSend(DelayedQueueConfig.DELAYED_EXCHANGE_NAME, DelayedQueueConfig.DELAYED_ROUTING_KEY, message,
                msg -> {
                    // 设置发送消息的延迟时长
                    msg.getMessageProperties().setDelay(delayTime);
                    return msg;
                }
            );
    }
}
