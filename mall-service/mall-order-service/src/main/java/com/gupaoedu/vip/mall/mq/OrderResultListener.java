package com.study.vip.mall.mq;

import com.alibaba.fastjson.JSON;
import com.study.vip.mall.order.service.OrderService;
import com.study.vip.mall.pay.model.PayLog;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Component
@RocketMQMessageListener(topic = "log",consumerGroup = "resultgroup")
public class OrderResultListener implements RocketMQListener, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private OrderService orderService;
    /***
     * 监听消息
     * 实现RocketMQPushConsumerLifecycleListener监听器之后，此方法不调用
     * @param message
     */
    @Override
    public void onMessage(Object message) {

    }

    /**
     * 消息监听
     * @param defaultMQPushConsumer
     */
    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        defaultMQPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                try {
                    for (MessageExt msg : msgs) {
                        String result = new String(msg.getBody(),"UTF-8");
                        System.out.println("result:"+result);
                        PayLog payLog = JSON.parseObject(result,PayLog.class);
                        if(payLog.getStatus().intValue()==2) {
                            orderService.updateAfterPayStatus(payLog.getId());
                        }else {
                            //支付失败
                            //一定的时间支付
                            //1:关闭支付，关闭过程中用户如果已支付，需要修改订单状态
                            //2:库存回滚
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //消费状态
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
    }
}
