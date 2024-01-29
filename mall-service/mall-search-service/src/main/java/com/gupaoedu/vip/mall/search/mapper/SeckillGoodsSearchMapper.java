package com.study.vip.mall.search.mapper;

import com.study.vip.mall.search.model.SeckillGoodsEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SeckillGoodsSearchMapper extends ElasticsearchRepository<SeckillGoodsEs,String> {
}
