package com.study.vip.mall.dw.feign;

import com.study.mall.util.RespResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(value = "mall-dw")
public interface HotGoodsFeign {
    /****
     * 热点商品查询
     */
    @PostMapping("/hot/goods/search/{size}/{hour}/{max}")
    RespResult<List<Map<String,String>>> searchHot(
            @PathVariable(value = "size")Integer size,
            @PathVariable(value = "hour")Integer hour,
            @PathVariable(value = "max")Integer max,
            @RequestBody(required = false) String[] ids);
}
