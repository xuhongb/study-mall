package com.study.vip.mall.api.filter;

import com.alibaba.fastjson.JSON;
import com.study.vip.mall.api.hot.HotQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;


public class RequestFilter implements GlobalFilter, Ordered {

    @Autowired
    private HotQueue hotQueue;
    /**
     * 拦截所有请求     http://localhost:9001/mall/seckill/order?id&num
     *  *                 JWT
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        //用户名
        String username = "gp";
        //商品ID
        String id = request.getQueryParams().getFirst("id");
        //数量
        Integer num =Integer.valueOf( request.getQueryParams().getFirst("num") );

        //排队结果
        int result = hotQueue.hotToQueue(username, id, num);

        //QUEUE_ING、HAS_QUEUE
        if(result==HotQueue.QUEUE_ING || result==HotQueue.HAS_QUEUE){
            //响应状态码200
            Map<String,Object> resultMap = new HashMap<String,Object>();
            resultMap.put("type","hot");
            resultMap.put("code",result);
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            exchange.getResponse().setComplete();
            exchange.getResponse().getHeaders().add("message", JSON.toJSONString(resultMap));
        }

        //NOT_HOT 直接由后端服务处理
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
