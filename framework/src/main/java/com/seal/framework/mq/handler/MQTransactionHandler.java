package com.seal.framework.mq.handler;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 用来处理事务消息的玩意
 */
public interface MQTransactionHandler {
    LocalTransactionState executeLocalTransaction(Message message, Object o);
    LocalTransactionState checkLocalTransaction(MessageExt messageExt);
}
