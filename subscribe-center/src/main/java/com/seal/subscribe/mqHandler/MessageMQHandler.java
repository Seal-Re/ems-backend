package com.seal.subscribe.mqHandler;
/*
import com.seal.framework.mq.annotation.MQHandlerActualizer;
import com.seal.framework.mq.handler.MQHandler;
import com.seal.framework.redis.util.RedisUtil;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Set;

@Component
@MQHandlerActualizer(topic = "message")
public class MessageMQHandler implements MQHandler {

    private final String KEY = "subscribe:";
    private final String topic = "message";
    private final int limit = 20;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ConsumeConcurrentlyStatus handle(String tag, MessageExt messageExt) {
        String messageStr = new String(messageExt.getBody());
        System.out.println(messageStr);

        try {
            Set<Object> urlList = redisUtil.getSetMembers(KEY+topic);
            System.out.println("--- Redis 中所有url数据 --- \ntopoc:" + topic);
            if (urlList != null && !urlList.isEmpty()) {
                for (Object url : urlList) {

                    System.out.println("url: " + url);

                    String key = "affair:" + topic + ":" + url;
                    redisUtil.addMessageWithLimit(key, messageStr, limit);//存储消息

                    broadcastUrl(url, messageStr);
                }
            } else {
                System.out.println("没有找到任何url数据。");
            }
            System.out.println("-------------------------");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("遍历 Redis 数据时出错。");
        }

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    void broadcastUrl(Object url, String message) {
        if (url == null || !(url instanceof String)) {
            System.out.println("url 格式/类型 烂了");
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(message, headers);

        try {
            restTemplate.postForEntity(url.toString(), request, String.class);
            System.err.println("发送消息到 " + url + " 时发生正确");
        } catch (HttpClientErrorException e) {
            System.err.println("发送消息到 " + url + " 时http客户端发生错误");
            System.err.println("状态码: " + e.getStatusCode());
            System.err.println("响应体: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("发送消息到 " + url + " 时发生错误");
            e.printStackTrace();
        }
    }

}*/