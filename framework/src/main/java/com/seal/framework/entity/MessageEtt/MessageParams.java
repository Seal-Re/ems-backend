package com.seal.framework.entity.MessageEtt;

import lombok.Data;
import java.util.List;

@Data
public class MessageParams {

    private String ability;
    private String sendTime;
    private List<MessageEvents> events;

}
