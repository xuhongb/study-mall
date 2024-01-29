package com.study.vip.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {"com.study.vip.mall.seckill.feign","com.study.vip.mall.goods.feign"})
public class MallWebPageApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallWebPageApplication.class,args);
    }
}
