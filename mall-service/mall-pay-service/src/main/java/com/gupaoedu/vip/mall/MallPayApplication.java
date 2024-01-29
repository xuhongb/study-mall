package com.study.vip.mall;

import com.github.wxpay.sdk.WXPay;
import com.study.vip.mall.pay.config.WeixinPayConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan(basePackages = {"com.study.vip.mall.pay.mapper"})
public class MallPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallPayApplication.class,args);
    }

    /****
     * 微信支付SDK对象
     * @param weixinPayConfig
     * @return
     * @throws Exception
     */
    @Bean
    public WXPay wxPay(WeixinPayConfig weixinPayConfig) throws Exception {
        return new WXPay(weixinPayConfig);
    }
}
