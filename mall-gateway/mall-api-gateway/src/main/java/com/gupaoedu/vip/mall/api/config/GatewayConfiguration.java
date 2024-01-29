package com.study.vip.mall.api.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class GatewayConfiguration {

    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public GatewayConfiguration(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    /**
     * 限流的异常处理器
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    /***
     * Sentinel路由处理核心过滤器
     * @return
     */
    @Bean
    @Order(-1)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    /***
     * 规则和Api加载
     */
    @PostConstruct
    public void doInit(){
        initCustomizedApis();
        initGatewayRules();
    }

    /***
     * Api定义组
     */
    private void initCustomizedApis() {
        //定义集合存储要定义的Api集合
        Set<ApiDefinition> definitions = new HashSet<ApiDefinition>();
        //创建每个api,并配置相关规律
        ApiDefinition cartApi = new ApiDefinition("mall_cart_api")
                .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                    // /cart/list
                    add(new ApiPathPredicateItem().setPattern("/cart/list"));
                    // /cart/*/*
                    add(new ApiPathPredicateItem().setPattern("/cart/**")
                            //参数值的匹配策略
                            // 精确匹配（PARAM_MATCH_STRATEGY_EXACT）
                            // 子串匹配（PARAM_MATCH_STRATEGY_CONTAINS）
                            // 正则匹配（PARAM_MATCH_STRATEGY_REGEX）
                            .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                }});
        //将创建好的Api添加到api集合中
        definitions.add(cartApi);
        //加载Api到sentinel
        GatewayApiDefinitionManager.loadApiDefinitions(definitions);
    }


    /***
     * 限流规则定义
     */
    private void initGatewayRules() {
        //网关限流规则 创建集合存储所有规则
        Set<GatewayFlowRule> rules = new HashSet<GatewayFlowRule>();

        //商品微服务规则配置
        //资源名称，可以是网关中的 route 名称或者用户自定义的 API 分组名称
        rules.add(new GatewayFlowRule("goods_route")
                //限流阈值
                .setCount(2)
                //应对突发请求时额外允许的请求数目。
                .setBurst(2)
                //统计时间窗口，单位是秒，默认是 1 秒。
                .setIntervalSec(1)
                //限流行为
                //CONTROL_BEHAVIOR_RATE_LIMITER 匀速排队
                //CONTROL_BEHAVIOR_DEFAULT 快速失败(默认)
                .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER)
                //匀速排队模式下的最长排队时间，单位是毫秒，仅在匀速排队模式下生效。
                .setMaxQueueingTimeoutMs(1000)
        );
        //API 【mall_cart_api】限流配置
        rules.add(new GatewayFlowRule("mall_cart_api")
                //限流阈值
                .setCount(3)
                //统计时间窗口，单位是秒，默认是 1 秒。
                .setIntervalSec(1)
        );
        //加载网关规则
        GatewayRuleManager.loadRules(rules);
    }
}
