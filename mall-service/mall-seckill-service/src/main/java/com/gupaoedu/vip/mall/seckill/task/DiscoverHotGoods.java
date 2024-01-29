package com.study.vip.mall.seckill.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.elasticjob.lite.annotation.ElasticSimpleJob;
import com.study.mall.util.RespResult;
import com.study.vip.mall.dw.feign.HotGoodsFeign;
import com.study.vip.mall.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@ElasticSimpleJob(
        jobName = "${elaticjob.zookeeper.namespace}",
        shardingTotalCount = 1,
        cron = "0/10 * * * * ? *"
)
@Component
public class DiscoverHotGoods implements SimpleJob {

    @Autowired
    private HotGoodsFeign hotGoodsFeign;

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    //热门数据条件
    @Value("${hot.size}")
    private Integer size;
    @Value("${hot.hour}")
    private Integer hour;
    @Value("${hot.max}")
    private Integer max;

    @Override
    public void execute(ShardingContext shardingContext) {
        //远程调用
        String[] ids=isolationList();
        RespResult<List<Map<String, String>>> listRespResult = hotGoodsFeign.searchHot(size, hour, max, ids);
        //集合数据获取
        List<Map<String, String>> listData = listRespResult.getData();
        //结果信息
        for (Map<String, String> dataMap : listData) {
            //处理请求路径
            String uri =uriReplace( dataMap.get("uri") , 1);
            System.out.println("查询到的商品ID："+uri);
            //隔离
            seckillGoodsService.isolation(uri);
        }
    }

    /***
     * 查询已经被隔离的热点商品
     */
    public String[] isolationList(){
        //获取所有已经被隔离的热门商品ID
        Set<String> ids = redisTemplate.boundHashOps("HotSeckillGoods").keys();
        String[] allids =new String[ids.size()];
        ids.toArray(allids);
        //uri地址处理
        for (int i = 0; i < allids.length; i++) {
            allids[i] = uriReplace(allids[i],2);
        }
        return allids;
    }

    /***
     * uri处理
     * @param uri
     * @param type：1 uri表示商品请求路径， 2 uri表示商品ID
     * @return
     */
    public String uriReplace(String uri,Integer type){
        switch (type){
            case 1:
                uri=uri.replace("/msitems/","").replace(".html","");
                break;
            case 2:
                uri="/msitems/"+uri+".html";
                break;
            default:
                uri="/msitems/"+uri+".html";
        }
        return uri;
    }
}