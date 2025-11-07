package com.seal.affair.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.seal.affair.service.AffairService;
import com.seal.framework.domain.Result;
import com.seal.framework.entity.MessageEtt.MessageEtt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class AffairController {

    @Autowired
    private AffairService affairService;

    @PostMapping("/addDefault")
    public Result addAffairDefault(@RequestBody MessageEtt messageEtt){
        return Result.success(affairService.addAffair(messageEtt,1));
    }

    @PostMapping("/addTransaction")
    public Result addAffairTransaction(@RequestBody MessageEtt messageEtt){
        return Result.success(affairService.addAffair(messageEtt,0));
    }

}

