package com.study.vip.mall.search.controller;

import com.study.mall.util.RespResult;
import com.study.mall.util.UrlUtils;
import com.study.vip.mall.search.feign.SkuSearchFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping(value = "/web/search")
public class SearchController {

    @Autowired
    private SkuSearchFeign skuSearchFeign;
    /**
     * 搜索
     */
    @GetMapping
    public String search(@RequestParam(required = false)Map<String, Object> searchMap, Model model){
        //搜索
        RespResult<Map<String, Object>> resultMap = skuSearchFeign.search(searchMap);
        //组装用户访问的url
        model.addAttribute("url", UrlUtils.map2url("/web/search",searchMap,"page"));
        model.addAttribute("urlsort", UrlUtils.map2url("/web/search",searchMap,"sm","sfield","page"));
        model.addAttribute("result",resultMap.getData());
        model.addAttribute("searchMap",searchMap);
        return "search";
    }
}
