package com.study.vip.mall.pay.mq;

import com.study.mall.util.Signature;
import com.study.vip.mall.pay.service.WeixinPayService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*****
 * @Author:
 * @Description:
 ****/
@Component
@RocketMQMessageListener(topic = "refund",consumerGroup = "refundgroup")
public class RefundResultListener implements RocketMQListener,RocketMQPushConsumerLifecycleListener {

    @Autowired
    private Signature signature;

    @Autowired
    private WeixinPayService weixinPayService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void onMessage(Object message) {
    }

    /****
     * 消息监听
     * @param consumer
     */
    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.println("消息到达！");
                try {
                    for (MessageExt msg : msgs) {
                        //AES加密字符串
                        String result = new String(msg.getBody(),"UTF-8");
                        System.out.println("退款申请状态--*************:");
                        //解密
                        Map<String, String> map = signature.security(result);
                        //发送请求执行退款申请
                        if(map!=null){
                            //执行退款申请
                            Map<String, String>  resultMap = weixinPayService.refund(map);
                            //测试成功
                            //Map<String, String>  resultMap = new HashMap<>();
                            //resultMap.put("aa","OKOKOKOKOK");
                            System.out.println("退款申请状态---refundMap:"+resultMap);
                            //执行事务通知
                            Message message = MessageBuilder.withPayload(resultMap).build();
                            rocketMQTemplate.sendMessageInTransaction("refundstatustx",
                                    "refundstatus",
                                    message,
                                    resultMap);
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
    }
}
