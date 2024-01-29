package com.study.vip.mall.dw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.vip.mall.dw.model.HotGoods;
import com.study.vip.mall.dw.util.DruidPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HotGoodsService extends IService<HotGoods> {

    /****
     * 时间查询+排除+聚合
     */
    List<Map<String,String>> searchHotGoods(Integer size, Integer hour, String[] urls, Integer max);

    /****
     * 时间查询+排除
     */
    List<HotGoods> search(Integer size, Integer hour, String[] ids);
    /**
     * 查询过去几小时前N条记录
     * @param size
     * @param hour
     * @return
     */
    List<HotGoods> search(Integer size, Integer hour);


    /**
     * 分页查询+排序
     * @param page
     * @param size
     * @param sort
     * @param sortType
     * @return
     */
    DruidPage<List<HotGoods>> pageListSort(Integer page, Integer size, String sort, String sortType);

    /**
     * 分页查询
     */
    public DruidPage<List<HotGoods>> pageList(Integer size, Integer page);

    /**
     * 前N条
     * @param size
     * @return
     */
    List<HotGoods> topNum(Integer size);
}
