package com.seal.framework.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seal.framework.entity.MessageEtt.MessageEtt;
import org.springframework.stereotype.Component;

@Component
public class MessageUtils {

    public MessageEtt create(String messageStr) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            MessageEtt messageEtt = mapper.readValue(messageStr, MessageEtt.class);
            return messageEtt;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        /*
        JSONObject jsonObject = JSONObject.parseObject(message);

        String method =  jsonObject.getString("method");
        JSONObject params = jsonObject.getJSONObject("params");

        String ability =  params.getString("ability");
        String sendTime = params.getString("sendTime");
        JSONArray events = params.getJSONArray("events");

        for (Object event : events) {
            JSONObject eventJson = (JSONObject) event;

            String eventId = eventJson.getString("eventId");
            Integer eventType = eventJson.getInteger("eventType");
            String happenTime  = eventJson.getString("happenTime");
            String srcIndex = eventJson.getString("srcIndex");
            String srcName = eventJson.getString("srcName");
            String srcParentIndex = eventJson.getString("srcParentIndex");
            Integer srcType = eventJson.getInteger("srcType");
            Integer timeout = eventJson.getInteger("timeout");

        }*/
    }

}
