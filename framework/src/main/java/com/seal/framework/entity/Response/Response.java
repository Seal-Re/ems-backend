package com.seal.framework.entity.Response;

import lombok.Data;

@Data
public class Response {
    private String code;
    private String msg;
    private ResponseData data;

}