package com.seal.framework.utils;

import com.seal.framework.entity.Response.EventDetail;
import com.seal.framework.entity.Response.ResponseData;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResponseDataUtils {

    public ResponseData create(String eventDest, List<EventDetail> detail) {
        ResponseData responseData = new ResponseData();
        responseData.setEventDest(eventDest);
        responseData.setDetail(detail);
        return responseData;
    }

}
