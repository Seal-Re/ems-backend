package com.seal.framework.mq.handler;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 消息处理类
 */
public interface MQHandler {
    ConsumeConcurrentlyStatus handle(String tag, MessageExt messageExt);
}
