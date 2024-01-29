package com.study.vip.mall.dw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.vip.mall.dw.model.HotGoods;
import com.study.vip.mall.dw.util.DruidPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface HotGoodsMapper extends BaseMapper<HotGoods> {

    /****
     * 分组、聚合判断、TopN、时间判断、排序
     * 1:时间限制  最近1小时内
     * 2:查询数量
     * 3:根据查询数量排序
     * 4:如果已经是分析过的热门商品，需要把它排除
     */
    @Select("SELECT uri,count(*) as viewCount FROM mslogs WHERE __time>=TIMESTAMP '${time}' AND uri NOT IN ('${urls}') GROUP BY uri HAVING viewCount>#{max} ORDER BY viewCount desc LIMIT #{size}")
    List<Map<String,String>> searchHotGoods(@Param("size") Integer size,
                                            @Param("time") String time,
                                            @Param("urls")String urls,
                                            @Param("max")Integer max);

    /***
     * 数据搜索
     * @param size
     * @param time
     * @param urls
     * @return
     */
    @Select("select uri,__time as accesstime,ip from mslogs where __time>=TIMESTAMP '${time}' and uri not in('${urls}') limit #{size}")
    List<HotGoods> searchExclude(@Param("size") Integer size,@Param("time") String time,@Param("urls")String urls);

    /***
     * 搜索数据
     * @param size
     * @param time
     * @return
     */
    @Select("select uri,__time as accesstime,ip from mslogs where __time>=TIMESTAMP '${time}' limit #{size}")
    List<HotGoods> search(@Param("size") Integer size,@Param("time") String time);

    /***
     * 排序+分页
     * @param druidPage
     * @return
     */
    @Select("select uri,__time as accesstime,ip from mslogs order by ${sort} ${sortType} limit #{size} offset #{offset}")
    List<HotGoods> pageListSort(DruidPage<List<HotGoods>> druidPage);

    /**
     * 分页查询
     */
    @Select("SELECT uri,__time as accesstime,ip from mslogs limit #{size} offset #{offset}")
    List<HotGoods> pageList(@Param("size")Integer size,@Param("offset")Long offset);
    /**
     * topNum:前N条记录
     */
    @Select("SELECT uri,__time as accesstime,ip from mslogs limit #{size}")
    List<HotGoods> topNum(Integer size);
}
