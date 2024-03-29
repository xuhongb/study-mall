package com.study.vip.mall.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    /***
     * 创建RedissonClient客户端
     * @return
     */
    /***
     * 创建RedissonClient对象
     *      创建锁、解锁
     * @return
     */
    @Bean
    public RedissonClient redissonClient(){
        //创建Config
        Config config = new Config();
        //集群实现
        //config.useClusterServers().setScanInterval(2000).addNodeAddress("xxxx");
        config.useSingleServer().setAddress("redis://39.99.247.124:6379").setPassword("dF@2024");
        //创建RedissonClient
        return Redisson.create(config);
    }
}
