package com.study.vip.mall.cart.controller;

import com.google.common.collect.Lists;
import com.study.mall.util.RespResult;
import com.study.vip.mall.cart.model.Cart;
import com.study.vip.mall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /***
     * 删除指定购物车
     */
    @DeleteMapping
    public RespResult delete(@RequestBody List<String> ids){
        //删除购物车集合
        cartService.delete(ids);
        return RespResult.ok();
    }

    /***
     * 购物车数据
     */
    @PostMapping(value = "/list")
    public RespResult<List<Cart>> list(@RequestBody List<String> ids){
        //购物车集合
        List<Cart> carts = Lists.newArrayList(cartService.list(ids));
        return RespResult.ok(carts);
    }
    /**
     * 增加购物车方法
     */
    @GetMapping(value = "/{id}/{num}")
    public RespResult add(@PathVariable(value = "id")String id,
                          @PathVariable(value = "num")Integer num){
        String userName ="gp";
        cartService.add(id,userName,num);
        return RespResult.ok();
    }

    /**
     * 增加购物车方法
     */
    @GetMapping(value = "/list")
    public RespResult list(){
        String userName ="gp";
        List<Cart> list = cartService.list(userName);
        return RespResult.ok(list);
    }
}
