package com.study.vip.mall.search.controller;

import com.study.mall.util.RespResult;
import com.study.vip.mall.search.model.SkuEs;
import com.study.vip.mall.search.service.SkuSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/search")
public class SkuSearchController {
    @Autowired
    private SkuSearchService skuSearchService;

    /***
     * 商品搜索
     */
    @GetMapping
    public RespResult<Map<String,Object>> search(@RequestParam(required = false)Map<String,Object> searchMap){
        Map<String, Object> resultMap = skuSearchService.search(searchMap);
        return RespResult.ok(resultMap);
    }
    /****
     * 单个商品导入
     */
    @PostMapping(value = "/add")
    public RespResult add(@RequestBody SkuEs skuEs){
        skuSearchService.add(skuEs);
        return RespResult.ok();
    }

    /***
     * 根据ID删除
     * @param id
     * @return
     */
    @DeleteMapping(value = "/del/{id}")
    public RespResult del(@PathVariable("id")String id){
        skuSearchService.del(id);
        return RespResult.ok();
    }
}
