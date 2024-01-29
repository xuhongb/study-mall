package com.study.vip.mall.search.service;

import com.study.vip.mall.search.model.SeckillGoodsEs;

public interface SeckillGoodsSearchService {
    /***
     * 导入数据到ES中
     * @param seckillGoodsEs
     */
    void add(SeckillGoodsEs seckillGoodsEs);
}
