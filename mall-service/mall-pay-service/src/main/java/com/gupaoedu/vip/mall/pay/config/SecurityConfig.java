package com.study.vip.mall.pay.config;

import com.study.mall.util.Signature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    //秘钥
    @Value("${payconfig.aes.skey}")
    private String skey;

    //验签加盐值
    @Value("${payconfig.aes.salt}")
    private String salt;

    /***
     * 验签对象
     */
    @Bean(value = "signature")
    public Signature signature(){
        return new Signature(skey,salt);
    }
}
