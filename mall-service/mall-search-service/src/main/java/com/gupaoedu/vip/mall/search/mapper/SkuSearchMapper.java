package com.study.vip.mall.search.mapper;

import com.study.vip.mall.search.model.SkuEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SkuSearchMapper extends ElasticsearchRepository<SkuEs,String> {
}
