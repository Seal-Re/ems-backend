package com.seal.subscribe.controller;

import com.seal.subscribe.dto.SubscribeDto;
import com.seal.framework.entity.Response.Response;
import com.seal.subscribe.entity.Subscribe;
import com.seal.subscribe.service.SubscribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.seal.framework.utils.ApiResponse;

import java.util.List;

@RestController
@RequestMapping
public class SubscribeController {

    @Autowired
    private SubscribeService subscribeService;

    @PostMapping("/add")
    public ApiResponse<Response> subscribeAdd(@RequestBody SubscribeDto subscribeDto) {
        return ApiResponse.success(subscribeService.addSubscribe(subscribeDto));
    }

    @PostMapping("/delete")
    public ApiResponse<Response> subscribeDel(@RequestBody SubscribeDto subscribeDto) {
        return ApiResponse.success(subscribeService.delSubscribe(subscribeDto));
    }

    @GetMapping("/detect")
    public ApiResponse<List<Subscribe>> subscribeDetect() {
        return ApiResponse.success(subscribeService.detectSubscribe());
    }

    @GetMapping("/getMsg")
    public ApiResponse<List<String>> getMsg(@RequestParam String topic, @RequestParam String url, @RequestParam Integer N) {
        return ApiResponse.success(subscribeService.getMessages(topic, url, N));
    }

    @GetMapping("/getLatestMsg")
    public ApiResponse<List<String>> getLatestMsg() {
        return ApiResponse.success(subscribeService.getLatestMsg());
    }
}
