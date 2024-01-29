package com.study.vip.mall.search.controller;

import com.study.mall.util.RespResult;
import com.study.vip.mall.search.model.SeckillGoodsEs;
import com.study.vip.mall.search.service.SeckillGoodsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/seckill/goods")
public class SeckillGoodsSearchController {

    @Autowired
    private SeckillGoodsSearchService seckillGoodsSearchService;

    /***
     * 导入数据到索引库
     */
    @PostMapping(value = "/import")
    public RespResult add(@RequestBody SeckillGoodsEs seckillGoodsEs){
        seckillGoodsSearchService.add(seckillGoodsEs);
        return RespResult.ok();
    }
}
