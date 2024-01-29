package com.study.vip.mall.seckill.controller;

import com.study.mall.util.RespResult;
import com.study.vip.mall.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/seckill/order")
public class SeckillOrderController {

    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 非热门抢单
     * @param username
     * @param id
     * @param num
     * @return
     */
    @PostMapping
    public RespResult add(String username,String id,Integer num){
        return RespResult.ok("抢单成功");
    }
}
