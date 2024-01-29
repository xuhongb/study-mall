package com.study.vip.mall.cart.feign;

import com.study.mall.util.RespResult;
import com.study.vip.mall.cart.model.Cart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "mall-cart")
public interface CartFeign {

    /***
     * 删除指定购物车
     */
    @DeleteMapping(value = "/cart")
    RespResult delete(@RequestBody List<String> ids);
    /***
     * 购物车数据
     */
    @PostMapping(value = "/cart/list")
    RespResult<List<Cart>> list(@RequestBody List<String> ids);
}
