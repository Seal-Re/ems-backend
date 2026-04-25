package com.seal.subscribe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seal.subscribe.dto.SubscribeDto;
import com.seal.framework.entity.Response.Response;
import com.seal.subscribe.entity.Subscribe;

import java.util.List;

public interface SubscribeService extends IService<Subscribe> {

    Response addSubscribe(SubscribeDto subscribeDto);

    Response delSubscribe(SubscribeDto subscribeDto);

    List<Subscribe> detectSubscribe();

    List<String> getMessages(String topic, String url, Integer N);

    List<String> getLatestMsg();
}
