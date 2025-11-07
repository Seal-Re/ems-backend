package com.seal.framework.entity.MessageEtt;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class MessageEvents {

    private JsonNode data;
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
