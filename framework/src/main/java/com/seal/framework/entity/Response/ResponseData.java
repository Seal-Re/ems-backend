package com.seal.framework.entity.Response;

import lombok.Data;
import java.util.List;

@Data
public class ResponseData {
    private List<EventDetail> detail;
    private String eventDest;
}