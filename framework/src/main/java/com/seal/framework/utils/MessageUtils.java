package com.seal.framework.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seal.framework.entity.MessageEtt.MessageEtt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MessageUtils {

    private static final Logger log = LoggerFactory.getLogger(MessageUtils.class);

    private final ObjectMapper mapper = new ObjectMapper();

    public MessageEtt create(String messageStr) {
        try {
            return mapper.readValue(messageStr, MessageEtt.class);
        } catch (Exception e) {
            log.error("消息反序列化失败: {}", messageStr, e);
            return null;
        }
    }
}
