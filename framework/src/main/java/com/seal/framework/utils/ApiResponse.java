package com.seal.framework.utils;

import lombok.Data;

@Data
public class ApiResponse<T> {

    // 状态码
    private int code;

    // 提示信息
    private String message;

    // 实际返回的数据
    private T data;

    // 私有构造函数，防止外部直接创建
    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功响应的工厂方法
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "success", null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    // 失败响应的工厂方法
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    // 常见失败响应
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null);
    }

}