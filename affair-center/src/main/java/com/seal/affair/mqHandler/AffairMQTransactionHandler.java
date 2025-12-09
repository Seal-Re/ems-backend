package com.seal.affair.mqHandler;

import com.alibaba.fastjson.JSONObject;
import com.seal.affair.service.AffairService;
import com.seal.framework.entity.MessageEtt.MessageEtt;
import com.seal.framework.mq.annotation.MQHandlerActualizer;
import com.seal.framework.mq.handler.MQTransactionHandler;
import com.seal.framework.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.seal.framework.utils.MD5Util.getMD5;

@Component
@Slf4j
@MQHandlerActualizer(topic = "all")
public class AffairMQTransactionHandler implements MQTransactionHandler {

    @Autowired
    private AffairService affairService;

    @Autowired
    private RedisUtil redisUtil;

    private final int LIMIT = 100;

    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        LocalTransactionState state;
        String transactionId = null;

        try {
            String body = new String(message.getBody());
            MessageEtt messageEtt = JSONObject.parseObject(body, MessageEtt.class);

            transactionId = getMD5(messageEtt.toString());

            String redisKey = "rocketmq:transaction:" + transactionId;
            redisUtil.setWithExpire(redisKey, "COMMIT", 5, TimeUnit.MINUTES);

            state = LocalTransactionState.COMMIT_MESSAGE;
            log.info("本地事务执行成功，事务ID: {}", transactionId);
        } catch (Exception e) {
            log.error("本地事务执行失败，事务ID: {}", transactionId, e);
            // 如果出现异常，回滚消息
            state = LocalTransactionState.ROLLBACK_MESSAGE;
        }
        return state;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        LocalTransactionState state;
        String transactionId = null;
        System.out.println("111");
        try {
            String body = new String(messageExt.getBody());
            MessageEtt messageEtt = JSONObject.parseObject(body, MessageEtt.class);

            transactionId = getMD5(messageEtt.toString());

            String redisKey = "rocketmq:transaction:" + transactionId;

            Object transactionStatusExists = redisUtil.get(redisKey);

            if (transactionStatusExists != null) {
                state = LocalTransactionState.COMMIT_MESSAGE;
                log.info("本地事务回查成功，事务ID: {}", transactionId);
            } else {
                state = LocalTransactionState.ROLLBACK_MESSAGE;
                log.warn("本地事务回查失败，Redis中未找到事务ID: {}", transactionId);
            }
        } catch (Exception e) {
            log.error("回查逻辑执行失败，事务ID: {}", transactionId, e);
            state = LocalTransactionState.UNKNOW;
        }
        return state;
    }
}
