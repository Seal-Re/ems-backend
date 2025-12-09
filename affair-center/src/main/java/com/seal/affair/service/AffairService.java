package com.seal.affair.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seal.framework.entity.MessageEtt.MessageEtt;


public interface AffairService {

    MessageEtt addAffair(MessageEtt affair, int code);
}
