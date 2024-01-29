package com.study.vip.mall.seckill.controller;

import com.study.mall.util.RespResult;
import com.study.vip.mall.seckill.model.SeckillActivity;
import com.study.vip.mall.seckill.service.SeckillActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/activity")
public class SeckillActivityController {

    @Autowired
    private SeckillActivityService seckillActivityService;

    /***
     * 未过期的活动列表
     */
    @GetMapping
    public RespResult<List<SeckillActivity>> list(){
        //有效的活动时间查询
        List<SeckillActivity> list = seckillActivityService.validActivity();
        return RespResult.ok(list);
    }
}
