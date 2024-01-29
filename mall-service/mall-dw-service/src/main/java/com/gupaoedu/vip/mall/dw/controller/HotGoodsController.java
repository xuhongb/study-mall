package com.study.vip.mall.dw.controller;

import com.study.mall.util.RespResult;
import com.study.vip.mall.dw.model.HotGoods;
import com.study.vip.mall.dw.service.HotGoodsService;
import com.study.vip.mall.dw.util.DruidPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/hot/goods")
public class HotGoodsController {

    @Autowired
    private HotGoodsService hotGoodsService;


    /****
     * 热点商品查询
     */
    @PostMapping("/search/{size}/{hour}/{max}")
    public RespResult<List<Map<String,String>>> searchHot(
            @PathVariable(value = "size")Integer size,
            @PathVariable(value = "hour")Integer hour,
            @PathVariable(value = "max")Integer max,
            @RequestBody(required = false) String[] ids){
        //集合查询前N条
        List<Map<String,String>> hotGoods = hotGoodsService.searchHotGoods(size,hour,ids,max);
        return RespResult.ok(hotGoods);
    }

    /****
     * 指定时间的历史数据查询,排除指定数据
     */
    @PostMapping("/search/{size}/{hour}")
    public RespResult<List<HotGoods>> history(@PathVariable(value = "size")Integer size,
                                              @PathVariable(value = "hour")Integer hour,@RequestBody String[] ids){
        //集合查询前N条
        List<HotGoods> hotGoods = hotGoodsService.search(size,hour,ids);
        return RespResult.ok(hotGoods);
    }

    /****
     * 指定时间的历史数据查询
     */
    @GetMapping("/search/{size}/{hour}")
    public RespResult<List<HotGoods>> history(@PathVariable(value = "size")Integer size,
                                              @PathVariable(value = "hour")Integer hour){
        //集合查询前N条
        List<HotGoods> hotGoods = hotGoodsService.search(size,hour);
        return RespResult.ok(hotGoods);
    }

    /***
     * 分页排序查询
     * @return
     */
    @GetMapping("/{page}/{size}/{sort}/{type}")
    public RespResult<DruidPage<List<HotGoods>>> page(@PathVariable(value = "page")Integer page,
                                                      @PathVariable(value = "size")Integer size,
                                                      @PathVariable(value = "sort")String sort,
                                                      @PathVariable(value = "type")String sortType){
        //集合查询
        DruidPage<List<HotGoods>> pageInfo = hotGoodsService.pageListSort(page,size,sort,sortType);
        return RespResult.ok(pageInfo);
    }

    /***
     * 分页查询
     */
    @GetMapping(value = "/page/{page}/{size}")
    public RespResult<DruidPage<List<HotGoods>>> pageList(
            @PathVariable(value = "page")Integer page,
            @PathVariable(value = "size")Integer size){
        DruidPage<List<HotGoods>> druidPage = hotGoodsService.pageList(size, page);
        return RespResult.ok(druidPage);
    }
    /***
     * 查询前N条记录
     * @return
     */
    @GetMapping("/top/{size}")
    public RespResult<List<HotGoods>> topNum(@PathVariable(value = "size")Integer size){
        //集合查询前N条
        List<HotGoods> hotGoods = hotGoodsService.topNum(size);
        return RespResult.ok(hotGoods);
    }

    @GetMapping
    public RespResult<List<HotGoods>> list(){
        List<HotGoods> list = hotGoodsService.list();
        return RespResult.ok(list);
    }
}
