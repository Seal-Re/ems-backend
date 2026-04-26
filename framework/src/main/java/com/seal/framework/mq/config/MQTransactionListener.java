package com.seal.framework.mq.config;

import cn.hutool.core.util.StrUtil;
import com.seal.framework.mq.annotation.MQHandlerActualizer;
import com.seal.framework.mq.handler.MQTransactionHandler;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@Component
public class MQTransactionListener implements TransactionListener {

    private static final Logger log = LoggerFactory.getLogger(MQTransactionListener.class);
    @Autowired
    private Map<String, MQTransactionHandler> mqTransactionHandlerMap;

    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        MQTransactionHandler mqTransactionHandler = getListenner(message.getTopic(),message.getTags());
        return mqTransactionHandler.executeLocalTransaction(message,o);
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        MQTransactionHandler mqTransactionHandler = getListenner(messageExt.getTopic(),messageExt.getTags());
        return mqTransactionHandler.checkLocalTransaction(messageExt);
    }

    private MQTransactionHandler getListenner(String topic,String tags) {
        MQTransactionHandler mqTransactionHandler = null;
        log.info("事务消息 topic={} tag={}", topic, tags);
        for (Map.Entry<String, MQTransactionHandler> entry : mqTransactionHandlerMap.entrySet()) {
            MQHandlerActualizer msgHandlerActualizer = entry.getValue().getClass().getAnnotation(MQHandlerActualizer.class);
            if (msgHandlerActualizer != null) {
                String annotationTopic  = msgHandlerActualizer.topic();
                String[] annotationTags = msgHandlerActualizer.tags();

                if(StrUtil.equals(annotationTopic,"all")){
                    //获取该实例
                    mqTransactionHandler = entry.getValue();
                    break;
                }
                if (!StrUtil.equals(topic,annotationTopic)) {
                    //非该主题处理类
                    continue;
                }
                if(StrUtil.equals(annotationTags[0],"*")){
                    //获取该实例
                    mqTransactionHandler = entry.getValue();
                    break;
                }
                boolean isContains = Arrays.asList(annotationTags).contains(tags);
                if(isContains){
                    //注解类中包含tag则获取该实例
                    mqTransactionHandler = entry.getValue();
                    break;
                }
            }
        }
        return mqTransactionHandler;
    }
}
