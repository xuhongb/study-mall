package com.study.vip.mall.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.vip.mall.cart.model.Cart;
import com.study.vip.mall.goods.model.Sku;

import java.util.List;

public interface SkuService extends IService<Sku> {

    /***
     * 库存递减
     * @param carts
     */
    void decount(List<Cart> carts);
    List<Sku> typeSkuItems(Integer id);

    void delTypeSkuItems(Integer id);

    List<Sku> updateTypeSkuItems(Integer id);
}
