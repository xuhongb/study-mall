package com.study.vip.canal.job.dynamic;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.study.vip.canal.spring.SpringContext;
import com.study.vip.mall.page.feign.SeckillPageFeign;

/***
 * 1：执行周期
 * 2：分片
 * 3：指定Zookeeper中的命名空间
 */
public class DynamicJob implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("DynamicJob");
        //静态页删除
        delete(shardingContext.getJobParameter());
    }

    /***
     * 执行静态页删除
     */
    public void delete(String acid){
        //从容器中获取指定的实例
        SeckillPageFeign seckillPageFeign = SpringContext.getBean(SeckillPageFeign.class);
        seckillPageFeign.deleByAct(acid);
    }
}
