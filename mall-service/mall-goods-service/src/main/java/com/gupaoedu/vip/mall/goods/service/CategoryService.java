package com.study.vip.mall.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.vip.mall.goods.model.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    /***
     * 根据分类父ID查询所有子类
     */
    List<Category> findByParentId(Integer pid);
}
