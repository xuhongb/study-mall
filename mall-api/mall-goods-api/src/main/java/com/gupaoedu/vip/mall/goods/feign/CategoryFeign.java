package com.study.vip.mall.goods.feign;

import com.study.mall.util.RespResult;
import com.study.vip.mall.goods.model.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "mall-goods")
public interface CategoryFeign {
    /**
     * 根据分类查询分类信息
     */
    @GetMapping(value = "/category/{id}")
    RespResult<Category> one(@PathVariable(value = "id")Integer id);
}
