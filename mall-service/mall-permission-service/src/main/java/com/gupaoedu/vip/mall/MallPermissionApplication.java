package com.study.vip.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.study.vip.mall.permission.mapper"})
public class MallPermissionApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallPermissionApplication.class,args);
    }
}
