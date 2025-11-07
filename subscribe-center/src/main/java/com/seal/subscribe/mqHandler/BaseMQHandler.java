package com.seal.subscribe.mqHandler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seal.framework.mq.annotation.MQHandlerActualizer;
import com.seal.framework.mq.handler.MQHandler;
import com.seal.framework.redis.util.RedisUtil;
import com.seal.framework.entity.MessageEtt.MessageEtt;
import com.seal.subscribe.entity.Subscribe;
import com.seal.subscribe.service.SubscribeService;
import com.seal.framework.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@Component
@Slf4j
@MQHandlerActualizer( topic = "all" )
public class BaseMQHandler implements MQHandler {

    private final String KEY = "subscribe:";
    private String topic;
    private final int limit = 20;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    SubscribeService subscribeService;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ConsumeConcurrentlyStatus handle(String tag, MessageExt messageExt) {
        topic =  messageExt.getTopic();
        String messageStr = new String(messageExt.getBody());
        System.out.println(messageStr);

        MessageEtt messageEtt = messageUtils.create(messageStr);

        try {

            QueryWrapper<Subscribe> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("event_types", topic);
            List<Subscribe> existingList = subscribeService.list(queryWrapper);

            for (Subscribe existing : existingList) {
                String url = existing.getEventDest();
                String key = "message:" + topic + ":" + url;

                redisUtil.addMessageWithLimit(key, messageStr, limit);//存储消息

                broadcastUrl(url, messageEtt);

                System.out.println("找到url：" + url);
                System.out.println("message：" + messageEtt);
            }

        }  catch (Exception e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    void broadcastUrl(Object url, MessageEtt messageEtt) {
        if (url == null || !(url instanceof String)) {
            System.out.println("url 格式/类型 烂了");
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        try {
            String jsonMessage = objectMapper.writeValueAsString(messageEtt);
            HttpEntity<String> request = new HttpEntity<>(jsonMessage, headers);

            restTemplate.postForEntity(url.toString(), request, String.class);
            System.out.println("发送消息到 " + url + " 时发生正确");
        } catch (HttpClientErrorException e) {
            System.err.println("发送消息到 " + url + " 时http客户端发生错误");
            System.err.println("状态码: " + e.getStatusCode());
            System.err.println("响应体: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("发送消息到 " + url + " 时发生错误");
            e.printStackTrace();
        }
    }

}