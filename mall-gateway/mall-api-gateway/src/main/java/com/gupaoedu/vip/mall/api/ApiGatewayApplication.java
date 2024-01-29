package com.study.vip.mall.api;

import com.study.vip.mall.api.limit.IpKeyResolver;
import com.study.vip.mall.api.limit.UriKeyResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class,args);
    }


    /***
     * IP限流
     * @return
     */
    @Primary
    @Bean(name="ipKeyResolver")
    public KeyResolver userIpKeyResolver() {
        return new IpKeyResolver();
    }

    /***
     * uri限流
     * @return
     */
    @Bean(name="uriKeyResolver")
    public KeyResolver userUriKeyResolver() {
        return new UriKeyResolver();
    }
}
