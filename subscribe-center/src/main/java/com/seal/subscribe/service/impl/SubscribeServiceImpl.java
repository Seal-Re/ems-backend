package com.seal.subscribe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seal.framework.redis.util.RedisUtil;
import com.seal.subscribe.dto.SubscribeDto;
import com.seal.framework.entity.Response.EventDetail;
import com.seal.framework.entity.Response.Response;
import com.seal.subscribe.entity.Subscribe;
import com.seal.subscribe.mapper.SubscribeMapper;
import com.seal.subscribe.service.SubscribeService;
import com.seal.framework.utils.EventDetailUtils;
import com.seal.framework.utils.ResponseDataUtils;
import com.seal.framework.utils.ResponseUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class SubscribeServiceImpl extends ServiceImpl<SubscribeMapper, Subscribe> implements SubscribeService {

    @Autowired
    private RedisUtil redisUtil;

    ResponseUtils responseUtils;
    EventDetailUtils eventDetailUtils;
    ResponseDataUtils responseDataUtils;

    public Response addSubscribe(SubscribeDto subscribeDto) {
        if (subscribeDto == null) {
            log.error("subscribe dto is null");
            return responseUtils.error("subscribe dto is null");
        }

        List<String> eventTypes = subscribeDto.getEventTypes();
        List<Integer> eventLvl =  subscribeDto.getEventLvl();
        String eventDest =  subscribeDto.getEventDest();
        int subType = subscribeDto.getSubType();

        if (eventTypes == null || eventLvl == null || eventTypes.size() != eventLvl.size()) {
            log.error("eventTypes and eventLvl arrays do not match");
            return responseUtils.error("eventTypes and eventLvl arrays do not match");
        }

        QueryWrapper<Subscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("event_types", eventTypes);
        queryWrapper.eq("event_dest", eventDest);
        List<Subscribe> existingList = this.list(queryWrapper);

        Map<String, Subscribe> existingMap = new HashMap<>();
        for (Subscribe existing : existingList) {
            existingMap.put(existing.getEventTypes(), existing);
        }

        List<Subscribe> toSaveList = new ArrayList<>();
        List<Subscribe> toUpdateList = new ArrayList<>();

        List<EventDetail> eventDetail = new ArrayList<>();

        for (int i = 0; i < eventTypes.size(); i++) {
            Subscribe newSubscribe = new Subscribe();
            newSubscribe.setEventTypes(eventTypes.get(i));
            newSubscribe.setEventLvl(eventLvl.get(i));
            newSubscribe.setEventDest(eventDest);
            newSubscribe.setSubType(subType);

            if (existingMap.containsKey(eventTypes.get(i)) ) {
                Subscribe existing = existingMap.get(eventTypes.get(i));
                if (!(existing.getEventLvl() == eventLvl.get(i) && existing.getSubType() == subType)) {
                    existing.setEventLvl(eventLvl.get(i));
                    existing.setSubType(subType);
                    toUpdateList.add(existing);

                    eventDetail.add(eventDetailUtils.create(eventTypes.get(i), 1));
                }
            } else {
                toSaveList.add(newSubscribe);
                eventDetail.add(eventDetailUtils.create(eventTypes.get(i), 0));
            }
        }

        if (!toSaveList.isEmpty()) {
            this.saveBatch(toSaveList);
        }
        if (!toUpdateList.isEmpty()) {
            this.updateBatchById(toUpdateList);
        }

        return responseUtils.success(responseDataUtils.create(eventDest, eventDetail));
    }

    public Response delSubscribe(SubscribeDto subscribeDto) {
        if (subscribeDto == null) {
            log.error("subscribe dto is null");
            return responseUtils.error("subscribe dto is null");
        }

        List<String> eventTypes = subscribeDto.getEventTypes();
        String eventDest =  subscribeDto.getEventDest();

        if (eventTypes == null ) {
            log.error("eventTypes arrays do not match");
            return responseUtils.error("eventTypes arrays do not match");

        }

        QueryWrapper<Subscribe> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("event_types", eventTypes);
        queryWrapper.eq("event_dest", eventDest);
        List<Subscribe> existingList = this.list(queryWrapper);

        Map<String, Subscribe> existingMap = new HashMap<>();
        for (Subscribe existing : existingList) {
            existingMap.put(existing.getEventTypes(), existing);
        }
        List<Long> toDelList = new ArrayList<>();

        List<EventDetail> eventDetail = new ArrayList<>();

        for (int i = 0; i < eventTypes.size(); i++) {

            if (existingMap.containsKey(eventTypes.get(i))) {
                Subscribe existing = existingMap.get(eventTypes.get(i));
                toDelList.add(existing.getId());

                eventDetail.add(eventDetailUtils.create(eventTypes.get(i), 2));
            }
        }

        if (!toDelList.isEmpty()) {
            this.removeByIds(toDelList);
        }

        return responseUtils.success(responseDataUtils.create(eventDest, eventDetail));
    }

    @Override
    public List<Subscribe> detectSubscribe() {
        List<Subscribe> subscribeList = this.list();
        return subscribeList;
    }

    /**
     * 获取指定 topic 和 url 下最新的 N 条消息。
     * 对应 Redis 命令: LRANGE message:topic:url 0 N-1
     * * @param topic 消息主题
     * @param url 事件目标URL
     * @param N 要获取的消息数量
     * @return 最新的N条消息列表
     */
    @Override
    public List<String> getMessages(String topic, String url, Integer N) {
        if (N == null || N <= 0) {
            return Collections.emptyList();
        }

        String key = "message:" + topic + ":" + url;

        List<Object> rawMessages = redisUtil.getRedisTemplate().opsForList()
                .range(key, 0L, N.longValue() - 1);

        if (rawMessages == null || rawMessages.isEmpty()) {
            return Collections.emptyList();
        }

        return rawMessages.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }


}