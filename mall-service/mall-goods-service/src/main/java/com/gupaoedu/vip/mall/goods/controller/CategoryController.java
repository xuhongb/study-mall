package com.study.vip.mall.goods.controller;

import com.study.vip.mall.goods.service.CategoryService;
import com.study.mall.util.RespResult;
import com.study.vip.mall.goods.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/category")
@CrossOrigin
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /****
     * 根据分类父ID查询子分类
     */
    @GetMapping(value = "/parent/{id}")
    public RespResult<List<Category>> findByParentId(@PathVariable("id")Integer id){
        return RespResult.ok(categoryService.findByParentId(id));
    }

    /**
     * 根据分类查询分类信息
     */
    @GetMapping(value = "/{id}")
        public RespResult<Category> one(@PathVariable(value = "id")Integer id){
        Category category = categoryService.getById(id);
        return  RespResult.ok(category);
    }
}
