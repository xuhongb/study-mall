package com.study.vip.mall.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.vip.mall.goods.model.SkuAttribute;

import java.util.List;

public interface SkuAttributeService extends IService<SkuAttribute> {
    /***
     * 根据分类ID查询属性加集合
     */
    List<SkuAttribute> queryList(Integer id);
}
