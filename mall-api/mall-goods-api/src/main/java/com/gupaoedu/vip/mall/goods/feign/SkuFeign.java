package com.study.vip.mall.goods.feign;

import com.study.mall.util.RespResult;
import com.study.vip.mall.cart.model.Cart;
import com.study.vip.mall.goods.model.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "mall-goods")    //服务名字
public interface SkuFeign {

    /***
     * 库存递减
     * @param carts
     * @return
     */
    @PostMapping(value = "/sku/dcount")
    RespResult decount(List<Cart> carts);
    /**
     *根据ID查询商品详情
     */
    @GetMapping(value="/sku/{id}")
    RespResult<Sku> one(@PathVariable(value = "id")String id);

        /****
         * 根据推广分类查询推广产品列表
         */
    @GetMapping(value = "/sku/aditems/type")
    List<Sku> typeItems(@RequestParam(value = "id")Integer id);

    /****
     * 根据推广分类查询推广产品列表
     *
     */
    @DeleteMapping(value = "/sku/aditems/type")
    RespResult delTypeItems(@RequestParam(value = "id")Integer id);

    /****
     * 根据推广分类查询推广产品列表
     */
    @PutMapping(value = "/sku/aditems/type")
    RespResult updateTypeItems(@RequestParam(value = "id")Integer id);

}
