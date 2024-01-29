package com.study.vip.mall.page.service;

public interface SeckillPageService {
    /***
     * 生成静态页
     */
    void html(String id) throws Exception;

    //删除秒杀详情页
    void delete(String id);
}
