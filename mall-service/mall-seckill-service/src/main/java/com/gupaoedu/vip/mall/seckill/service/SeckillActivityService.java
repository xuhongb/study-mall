package com.study.vip.mall.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.vip.mall.seckill.model.SeckillActivity;

import java.util.List;

public interface SeckillActivityService extends IService<SeckillActivity> {

    //有效活动时间查询
    List<SeckillActivity> validActivity();
}
