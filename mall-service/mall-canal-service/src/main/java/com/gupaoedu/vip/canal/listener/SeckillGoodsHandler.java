package com.study.vip.canal.listener;

import com.alibaba.fastjson.JSON;
import com.study.vip.mall.page.feign.SeckillPageFeign;
import com.study.vip.mall.search.feign.SeckillGoodsSearchFeign;
import com.study.vip.mall.search.model.SeckillGoodsEs;
import com.study.vip.mall.seckill.model.SeckillGoods;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.javatool.canal.client.annotation.CanalTable;
import top.javatool.canal.client.handler.EntryHandler;

@CanalTable(value = "seckill_goods")
@Component
public class SeckillGoodsHandler implements EntryHandler<SeckillGoods> {

    @Autowired
    private SeckillGoodsSearchFeign seckillGoodsSearchFeign;

    @Autowired
    private SeckillPageFeign seckillPageFeign;

    /**
     * 增加数据
     * @param seckillGoods
     */
    @SneakyThrows
    @Override
    public void insert(SeckillGoods seckillGoods) {
        //静态页生成
        seckillPageFeign.page(seckillGoods.getId());
        //索引生成
        seckillGoodsSearchFeign.add(JSON.parseObject(JSON.toJSONString(seckillGoods), SeckillGoodsEs.class));
    }

    @SneakyThrows
    @Override
    public void update(SeckillGoods before, SeckillGoods after) {
        //静态页生成
        seckillPageFeign.page(after.getId());
        //索引生成
        seckillGoodsSearchFeign.add(JSON.parseObject(JSON.toJSONString(after), SeckillGoodsEs.class));
    }

    @Override
    public void delete(SeckillGoods seckillGoods) {
    }
}
