package com.study.vip.mall.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.study.vip.mall.seckill.service.SeckillOrderService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@RocketMQMessageListener(
        topic = "order-queue",
        consumerGroup = "orderqueue-consumer",
        selectorExpression = "*"
)
@Component
public class OrderQueueListener implements RocketMQListener {

    @Autowired
    private SeckillOrderService seckillOrderService;
    /**
     * 消息监听
     * @param message
     */
    @Override
    public void onMessage(Object message) {
        System.out.println("---"+message);
        seckillOrderService.add(JSON.parseObject(message.toString(), Map.class));
    }
}
