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

<<<<<<< HEAD
    private final int limit = 1000;   // 分类消息保留条数
    private final int limit1 = 100; // 全局最新消息保留条数
=======
    private final String KEY = "subscribe:";
    private String topic;
    private final int limit = 20;
>>>>>>> f65081ad70abcacc1de1cc85cbc347d9cf7b615b

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    SubscribeService subscribeService;

<<<<<<< HEAD
=======
    @Autowired
>>>>>>> f65081ad70abcacc1de1cc85cbc347d9cf7b615b
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ConsumeConcurrentlyStatus handle(String tag, MessageExt messageExt) {
<<<<<<< HEAD
        String topic = messageExt.getTopic();
        String messageStr = new String(messageExt.getBody());
        System.out.println("收到消息: " + messageStr);
=======
        topic =  messageExt.getTopic();
        String messageStr = new String(messageExt.getBody());
        System.out.println(messageStr);
>>>>>>> f65081ad70abcacc1de1cc85cbc347d9cf7b615b

        MessageEtt messageEtt = messageUtils.create(messageStr);

        try {
<<<<<<< HEAD
            String globalKey = "Global:Latest100";
            redisUtil.addMessageWithLimit(globalKey, messageStr, limit1);

            String topicKey = "Topic:" + topic;
            redisUtil.addMessageWithLimit(topicKey, messageStr, limit);

            log.info("Redis 存储完成: GlobalKey={}, TopicKey={}", globalKey, topicKey);

=======
>>>>>>> f65081ad70abcacc1de1cc85cbc347d9cf7b615b

            QueryWrapper<Subscribe> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("event_types", topic);
            List<Subscribe> existingList = subscribeService.list(queryWrapper);

            for (Subscribe existing : existingList) {
                String url = existing.getEventDest();
<<<<<<< HEAD

                broadcastUrl(url, messageEtt);
            }

        }  catch (Exception e) {
            log.error("消息处理异常", e);
=======
                String key = "message:" + topic + ":" + url;

                redisUtil.addMessageWithLimit(key, messageStr, limit);//存储消息

                broadcastUrl(url, messageEtt);

                System.out.println("找到url：" + url);
                System.out.println("message：" + messageEtt);
            }

        }  catch (Exception e) {
            e.printStackTrace();
>>>>>>> f65081ad70abcacc1de1cc85cbc347d9cf7b615b
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

<<<<<<< HEAD
    void broadcastUrl(Object urlObj, MessageEtt messageEtt) {
        if (urlObj == null || !(urlObj instanceof String)) {
            log.warn("无效的 URL: {}", urlObj);
            return;
        }
        String url = (String) urlObj;
=======
    void broadcastUrl(Object url, MessageEtt messageEtt) {
        if (url == null || !(url instanceof String)) {
            System.out.println("url 格式/类型 烂了");
            return;
        }
>>>>>>> f65081ad70abcacc1de1cc85cbc347d9cf7b615b

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

<<<<<<< HEAD
=======

>>>>>>> f65081ad70abcacc1de1cc85cbc347d9cf7b615b
        try {
            String jsonMessage = objectMapper.writeValueAsString(messageEtt);
            HttpEntity<String> request = new HttpEntity<>(jsonMessage, headers);

<<<<<<< HEAD
            restTemplate.postForEntity(url, request, String.class);
            log.info("推送成功 -> {}", url);
        } catch (HttpClientErrorException e) {
            log.error("推送失败 [HTTP {}]: {} -> Response: {}", e.getStatusCode(), url, e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("推送异常: {}", url, e);
        }
    }
=======
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

>>>>>>> f65081ad70abcacc1de1cc85cbc347d9cf7b615b
}