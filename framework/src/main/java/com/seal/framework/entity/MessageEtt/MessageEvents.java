package com.seal.framework.entity.MessageEtt;

import lombok.Data;

import java.util.Map;

@Data
public class MessageEvents {

    private Map<String, Object> data;
    private String eventId;
    private String eventType;
    private String happenTime;
    private String srcIndex;
    private String srcName;
    private String srcParentIndex;
    private String srcType;
    private Integer status;
    private Integer timeout;

}
