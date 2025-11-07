package com.seal.framework.mq.config;

import cn.hutool.core.util.StrUtil;
import com.seal.framework.mq.annotation.MQHandlerActualizer;
import com.seal.framework.mq.handler.MQHandler;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 并发消息监听器
 */

@Component
public class MQListenerConcurrently implements MessageListenerConcurrently {
    @Autowired
    private Map<String, MQHandler> mqHandlerMap;

    public MQListenerConcurrently(Map<String, MQHandler> mqHandlerMap) {
        this.mqHandlerMap = mqHandlerMap;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        if(CollectionUtils.isEmpty(list)){
            System.out.println("empty");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        MessageExt messageExt = list.get(0);

        System.out.println(list.size());
        int reconsume = messageExt.getReconsumeTimes();//获取已经重复投送次数
        System.out.println(reconsume);
        if(reconsume ==3){//消息已经重试了3次，需做告警处理，已经相关日志
            System.out.println("reconsume");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

        //业务逻辑
        String topic = messageExt.getTopic();
        String tags  = messageExt.getTags();
        System.out.println("接受到的消息主题为：" + topic + "; tag为：" + tags);
        MQHandler mqMsgHandler = null;
        //获取消息处理类中的topic和tag注解，根据topic和tag进行策略分发出来具体业务
        for (Map.Entry<String, MQHandler> entry : mqHandlerMap.entrySet()) {
            MQHandlerActualizer msgHandlerActualizer = entry.getValue().getClass().getAnnotation(MQHandlerActualizer.class);//反射获取监听器的注解
            if (msgHandlerActualizer == null) {
                //非消息处理类
                continue;
            }
            String annotationTopic = msgHandlerActualizer.topic();
            if(StrUtil.equals(annotationTopic,"all")){
                //获取该实例
                mqMsgHandler = entry.getValue();
                break;
            }
            if (!StrUtil.equals(topic,annotationTopic)) {
                //非该主题处理类
                continue;
            }
            String[] annotationTags = msgHandlerActualizer.tags();
            if(StrUtil.equals(annotationTags[0],"*")){
                //获取该实例
                mqMsgHandler = entry.getValue();
                break;
            }
            boolean isContains = Arrays.asList(annotationTags).contains(tags);
            if(isContains){
                //注解类中包含tag则获取该实例
                mqMsgHandler = entry.getValue();
                break;
            }
        }
        if (mqMsgHandler == null) {
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        ConsumeConcurrentlyStatus status = mqMsgHandler.handle(tags,messageExt);
        // 如果没有return success,consumer会重新消费该消息，直到return success
        return status;
    }
}
