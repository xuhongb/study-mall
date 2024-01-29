package com.study.vip.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.study.vip.mall.search.feign")
public class MallWebSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallWebSearchApplication.class,args);
    }
}
