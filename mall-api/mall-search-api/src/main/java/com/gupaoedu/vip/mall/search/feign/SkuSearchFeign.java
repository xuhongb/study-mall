package com.study.vip.mall.search.feign;

import com.study.mall.util.RespResult;
import com.study.vip.mall.search.model.SkuEs;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(value = "mall-search")
@RequestMapping(value = "/search")
public interface SkuSearchFeign {
    /***
     * 商品搜索
     */
    @GetMapping
    RespResult<Map<String,Object>> search(@RequestParam(required = false)Map<String,Object> searchMap);
        /****
         * 单个商品导入
         */
    @PostMapping(value = "/add")
    RespResult add(@RequestBody SkuEs skuEs);

    /***
     * 根据ID删除
     * @param id
     * @return
     */
    @DeleteMapping(value = "/del/{id}")
    RespResult del(@PathVariable("id")String id);
}
