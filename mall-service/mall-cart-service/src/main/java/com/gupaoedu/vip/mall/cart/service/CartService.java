package com.study.vip.mall.cart.service;

import com.study.vip.mall.cart.model.Cart;

import java.util.List;

public interface CartService {

    //删除购物车集合
    void delete(List<String> ids);

    /***
     * 根据ID集合查询购物车列表
     * @param ids
     * @return
     */
    Iterable<Cart> list(List<String> ids);
    /**
     * 购物车列表
     */
    public List<Cart> list(String userName);

    /**
     * 加入购物车
     */
    void add(String id,String userName,Integer num);


}
