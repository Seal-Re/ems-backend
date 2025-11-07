package com.seal.affair.mqHandler;

import com.seal.framework.mq.annotation.MQHandlerActualizer;
import com.seal.framework.mq.handler.MQHandler;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

@Component
@MQHandlerActualizer(topic = "affair")
public class AffairMQHandler implements MQHandler {

    /*
    ConsumeConcurrentlyStatus 是消费者（Consumer）并发消费时返回的一种状态枚举，它定义了两种可能的结果：
        CONSUME_SUCCESS：
            表示消息已被成功消费。
            消费者将向 Broker 确认，该消息可以被标记为已处理，之后不会再被重新投递。
        RECONSUME_LATER：
            表示消息本次消费失败，需要稍后再次尝试重新消费。
     */

    @Override
    public ConsumeConcurrentlyStatus handle(String tag, MessageExt messageExt) {
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;//default获取到直接返回接收cg
    }
}
