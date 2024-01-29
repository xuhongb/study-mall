package com.study.vip.mall.api.filter;

import com.alibaba.fastjson.JSON;
import com.study.vip.mall.api.hot.HotQueue;
import com.study.vip.mall.api.permission.AuthorizationInterceptor;
import com.study.vip.mall.api.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ApiFilter implements GlobalFilter, Ordered {

    @Autowired
    private HotQueue hotQueue;
    @Autowired
    private AuthorizationInterceptor authorizationInterceptor;

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
        //uri
        String uri = request.getURI().getPath();

        //过滤uri是否有效
//        if(!authorizationInterceptor.isInvalid(uri)){
//            endProcess(exchange,404,"url bad");
//            return chain.filter(exchange);
//        }

        //是否需要拦截
        if(!authorizationInterceptor.isIntercept(exchange)) {
            //无需拦截，放行
            return chain.filter(exchange);
        }

        //令牌校验
        Map<String, Object> resultMap = authorizationInterceptor.tokenIntercept(exchange);
        if(resultMap==null || !authorizationInterceptor.rolePermission(exchange,resultMap)){
            //令牌校验失败 或者没有权限
            endProcess(exchange,401,"Access denied");
            return chain.filter(exchange);
        }

        if(uri.equals("/seckill/order")){
            //秒杀过滤
            seckillFilter(exchange, request, resultMap.get("username").toString());
            return chain.filter(exchange);
        }

        //NOT_HOT 直接由后端服务处理
        return chain.filter(exchange);
    }

    /***
     * 秒杀过滤
     * @param exchange
     * @param request
     * @param username
     */
    private void seckillFilter(ServerWebExchange exchange, ServerHttpRequest request, String username) {
        //商品ID
        String id = request.getQueryParams().getFirst("id");
        //数量
        Integer num =Integer.valueOf( request.getQueryParams().getFirst("num") );

        //排队结果
        int result = hotQueue.hotToQueue(username, id, num);

        //QUEUE_ING、HAS_QUEUE
        if(result==HotQueue.QUEUE_ING || result==HotQueue.HAS_QUEUE){
            endProcess(exchange,result,"hot");
        }
    }

    /***
     * 结束程序
     * @param exchange
     * @param code
     * @param message
     */
    public void endProcess(ServerWebExchange exchange,Integer code,String message){
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("code",code);
        resultMap.put("message",message);
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        exchange.getResponse().setComplete();
        exchange.getResponse().getHeaders().add("message",JSON.toJSONString(resultMap));
    }

    @Override
    public int getOrder() {
        return 10001;
    }
}
