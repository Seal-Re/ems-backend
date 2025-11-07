package com.seal.subscribe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seal.subscribe.dto.SubscribeDto;
import com.seal.framework.entity.Response.Response;
import com.seal.subscribe.entity.Subscribe;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author seal
 * @since 2025-08-19
 */
public interface SubscribeService extends IService<Subscribe> {

    Response addSubscribe(SubscribeDto subscribeDto);

    Response delSubscribe(SubscribeDto subscribeDto);

    List<Subscribe> detectSubscribe();

    List<String> getMessages(String topic, String url, Integer N);

}
