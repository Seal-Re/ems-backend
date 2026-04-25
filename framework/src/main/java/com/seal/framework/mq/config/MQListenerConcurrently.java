package com.seal.framework.mq.config;

import cn.hutool.core.util.StrUtil;
import com.seal.framework.mq.annotation.MQHandlerActualizer;
import com.seal.framework.mq.handler.MQHandler;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class MQListenerConcurrently implements MessageListenerConcurrently {

    private static final Logger log = LoggerFactory.getLogger(MQListenerConcurrently.class);

    @Autowired
    private Map<String, MQHandler> mqHandlerMap;

    public MQListenerConcurrently(Map<String, MQHandler> mqHandlerMap) {
        this.mqHandlerMap = mqHandlerMap;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        if (CollectionUtils.isEmpty(list)) {
            log.debug("consumeMessage: empty batch");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        MessageExt messageExt = list.get(0);

        int reconsume = messageExt.getReconsumeTimes();
        log.debug("consumeMessage batchSize={} reconsumeTimes={}", list.size(), reconsume);

        if (reconsume == 3) {
            log.warn("消息已重试3次，丢弃处理 msgId={}", messageExt.getMsgId());
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

        String topic = messageExt.getTopic();
        String tags  = messageExt.getTags();
        log.info("接收到消息 topic={} tag={}", topic, tags);

        MQHandler mqMsgHandler = null;
        for (Map.Entry<String, MQHandler> entry : mqHandlerMap.entrySet()) {
            MQHandlerActualizer msgHandlerActualizer = entry.getValue().getClass().getAnnotation(MQHandlerActualizer.class);
            if (msgHandlerActualizer == null) {
                continue;
            }
            String annotationTopic = msgHandlerActualizer.topic();
            if (StrUtil.equals(annotationTopic, "all")) {
                mqMsgHandler = entry.getValue();
                break;
            }
            if (!StrUtil.equals(topic, annotationTopic)) {
                continue;
            }
            String[] annotationTags = msgHandlerActualizer.tags();
            if (StrUtil.equals(annotationTags[0], "*")) {
                mqMsgHandler = entry.getValue();
                break;
            }
            if (Arrays.asList(annotationTags).contains(tags)) {
                mqMsgHandler = entry.getValue();
                break;
            }
        }
        if (mqMsgHandler == null) {
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        return mqMsgHandler.handle(tags, messageExt);
    }
}
