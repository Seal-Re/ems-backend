package com.seal.framework.utils;

import com.seal.framework.entity.Response.EventDetail;
import org.springframework.stereotype.Component;

@Component
public class EventDetailUtils {

    private final String[] TYPES = {"ADD", "UPDATE", "DELETE"};

    public EventDetail create(String eventTypes, int type) {
        EventDetail eventDetail = new EventDetail();
        eventDetail.setEventTypes(eventTypes);
        eventDetail.setType(TYPES[type]);
        return eventDetail;
    }

}
