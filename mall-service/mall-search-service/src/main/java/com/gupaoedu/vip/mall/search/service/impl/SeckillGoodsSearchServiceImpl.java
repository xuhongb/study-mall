package com.study.vip.mall.search.service.impl;

import com.study.vip.mall.search.mapper.SeckillGoodsSearchMapper;
import com.study.vip.mall.search.model.SeckillGoodsEs;
import com.study.vip.mall.search.service.SeckillGoodsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeckillGoodsSearchServiceImpl implements SeckillGoodsSearchService {
    @Autowired
    private SeckillGoodsSearchMapper seckillGoodsSearchMapper;

    /***
     * 导入数据到ES中
     * @param seckillGoodsEs
     */
    @Override
    public void add(SeckillGoodsEs seckillGoodsEs) {
        seckillGoodsSearchMapper.save(seckillGoodsEs);
    }
}
