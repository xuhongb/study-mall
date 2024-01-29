package com.study.vip.mall.goods.controller;

import com.study.vip.mall.goods.service.SpuService;
import com.study.mall.util.RespResult;
import com.study.vip.mall.goods.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/spu")
@CrossOrigin
public class SpuController {
    @Autowired
    private SpuService spuService;

    /*****
     * 产品保存
     */
    @PostMapping(value = "/save")
    public RespResult save(@RequestBody Product product){
        spuService.saveProduct(product);
        return RespResult.ok();
    }

    /**
     * 查询product
     */
    @GetMapping(value = "/product/{id}")
    public RespResult<Product> one(@PathVariable(value = "id")String id){
        Product product = spuService.findBySupId(id);
        return RespResult.ok(product);
    }
}
