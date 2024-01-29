package com.study.vip.mall.goods.feign;

import com.study.mall.util.RespResult;
import com.study.vip.mall.goods.model.Product;
import com.study.vip.mall.goods.model.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "mall-goods")    //服务名字
public interface SpuFeign {
    /**
     * 查询product
     */
    @GetMapping(value = "/spu/product/{id}")
    RespResult<Product> one(@PathVariable(value = "id")String id);

}
