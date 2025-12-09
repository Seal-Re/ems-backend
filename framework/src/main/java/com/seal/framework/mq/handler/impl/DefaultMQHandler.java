package com.seal.framework.mq.handler.impl;

import com.seal.framework.mq.handler.MQHandler;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Service;

@Service
public class DefaultMQHandler implements MQHandler {
    @Override
    public ConsumeConcurrentlyStatus handle(String tag, MessageExt messageExt) {
        return null;
    }
}
