package com.study.vip.mall.search.feign;

import com.study.mall.util.RespResult;
import com.study.vip.mall.search.model.SeckillGoodsEs;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "mall-search")
public interface SeckillGoodsSearchFeign {

    /***
     * 导入数据到索引库
     */
    @PostMapping(value = "/seckill/goods/import")
    RespResult add(@RequestBody SeckillGoodsEs seckillGoodsEs);
}
