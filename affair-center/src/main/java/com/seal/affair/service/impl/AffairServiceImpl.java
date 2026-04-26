package com.seal.affair.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seal.affair.service.AffairService;
import com.seal.framework.entity.MessageEtt.MessageEvents;
import com.seal.framework.entity.MessageEtt.MessageEtt;
import com.seal.framework.entity.MessageEtt.MessageParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AffairServiceImpl implements AffairService {

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    @Autowired
    private TransactionMQProducer transactionMQProducer;

    @Override
    public MessageEtt addAffair(MessageEtt messageEtt, int code) {
        if (messageEtt == null || messageEtt.getParams() == null || messageEtt.getParams().getEvents() == null || messageEtt.getParams().getEvents().isEmpty()) {
            log.error("Invalid MessageEtt object: messageEtt or its events list is null or empty.");
            return null;
        }

        Map<String, List<MessageEvents>> eventsByTopic = messageEtt.getParams().getEvents()
                .stream()
                .collect(Collectors.groupingBy(MessageEvents::getEventType));//将 events 列表按照 eventType 分组

        for (Map.Entry<String, List<MessageEvents>> entry : eventsByTopic.entrySet()) {
            String topic = entry.getKey();
            List<MessageEvents> eventsForTopic = entry.getValue();

            MessageEtt newMsgEtt = new MessageEtt();
            MessageParams newMsgParams = new MessageParams();
            newMsgParams.setEvents(eventsForTopic);
            newMsgEtt.setParams(newMsgParams);
            newMsgEtt.setMethod(messageEtt.getMethod());

            String messageBody = JSON.toJSONString(newMsgEtt);
            byte[] bodyBytes = messageBody.getBytes();

            Message message = new Message(topic, "*", bodyBytes);

            log.info("topic: {} message: {}", topic, message);

            if (code == 1) {
                try {
                    SendResult sendResult = defaultMQProducer.send(message);
                    log.info("Default Producer Status: {}", sendResult.getSendStatus());
                    log.info("Message ID: {}", sendResult.getMsgId());
                } catch (MQBrokerException | RemotingException | InterruptedException | MQClientException e) {
                    log.error("Failed to send message via default producer.", e);
                    throw new RuntimeException("Failed to send default message.", e);
                }
            } else {
                try {
                    SendResult sendResult = transactionMQProducer.sendMessageInTransaction(message, null);
                    log.info("Transaction Producer Result: {}", sendResult);
                } catch (MQClientException e) {
                    log.error("Failed to send transaction message.", e);
                    throw new RuntimeException("Failed to send transaction message.", e);
                }
            }
        }

        return messageEtt;
    }
}