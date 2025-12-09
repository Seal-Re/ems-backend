package com.seal.framework.utils;

import com.seal.framework.entity.Response.Response;
import com.seal.framework.entity.Response.ResponseData;
import org.springframework.stereotype.Component;

@Component
public class ResponseUtils {

    public Response error() {
        Response response = new Response();
        response.setCode("1");
        response.setMsg("error");
        return response;
    }

    public Response error(String msg) {
        Response response = new Response();
        response.setCode("1");
        response.setMsg("error" + msg);
        return response;
    }

    public Response success(ResponseData responseData) {
        Response response = new Response();
        response.setCode("1");
        response.setMsg("success");
        response.setData(responseData);
        return response;
    }



}
