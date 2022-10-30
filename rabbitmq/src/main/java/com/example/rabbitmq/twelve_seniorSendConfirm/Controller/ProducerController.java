package com.example.rabbitmq.twelve_seniorSendConfirm.Controller;

import com.example.rabbitmq.eleven_delayQueue.config.DelayedQueueConfig;
import com.example.rabbitmq.twelve_seniorSendConfirm.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.Configuration;
import java.util.Date;

/**
 * <简述> 发布确认-高级-生产者
 * <详细描述> SendMsgController
 *
 * @author qianxiaoning
 * @version V1.0
 * @see
 * @since
 */
@Slf4j
@RestController
@RequestMapping("/confirm")
public class ProducerController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 开始发消息 测试确认 先跑通
    // 测试 http://localhost:8080/confirm/sendMessage/xxx
    @GetMapping("/sendMessage/{message}")
    public void sendMessage(@PathVariable String message){
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME,
                ConfirmConfig.CONFIRM_ROUTING_KEY, message);
        log.info("发送消息内容：{}", message);
    }

    @GetMapping("/sendMessage2/{message}")
    public void sendMessage2(@PathVariable String message){
        CorrelationData correlationData = new CorrelationData("1");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME,
                ConfirmConfig.CONFIRM_ROUTING_KEY, message + "key1", correlationData);
        log.info("发送消息内容：{}", message + "key1");
    }

    @GetMapping("/sendMessage3/{message}")
    public void sendMessage3(@PathVariable String message){
        CorrelationData correlationData = new CorrelationData("2");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME + "123",
                ConfirmConfig.CONFIRM_ROUTING_KEY, message, correlationData);
        log.info("发送消息内容：{}", message);
    }

    @GetMapping("/sendMessage4/{message}")
    public void sendMessage4(@PathVariable String message){
        CorrelationData correlationData = new CorrelationData("3");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME,
                ConfirmConfig.CONFIRM_ROUTING_KEY + "2", message + "key12", correlationData);
        log.info("发送消息内容：{}", message + "key12");
    }

    @GetMapping("/sendMessage5/{message}")
    public void sendMessage5(@PathVariable String message){
        CorrelationData correlationData = new CorrelationData("1");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME,
                ConfirmConfig.CONFIRM_ROUTING_KEY, message + "key1", correlationData);
        log.info("发送消息内容：{}", message + "key1");

        CorrelationData correlationData2 = new CorrelationData("3");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME2,
                ConfirmConfig.CONFIRM_ROUTING_KEY + "2", message + "key12", correlationData2);
        log.info("发送消息内容：{}", message + "key12");
    }
}
