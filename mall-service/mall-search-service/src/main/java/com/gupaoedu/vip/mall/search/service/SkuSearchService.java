package com.study.vip.mall.search.service;

import com.study.vip.mall.search.model.SkuEs;

import java.util.Map;

public interface SkuSearchService {
    /****
     * 搜索数据
     */
    Map<String,Object> search(Map<String,Object> searchMap);
    //添加索引
    void add(SkuEs skuEs);
    //删除索引
    void del(String id);
}
