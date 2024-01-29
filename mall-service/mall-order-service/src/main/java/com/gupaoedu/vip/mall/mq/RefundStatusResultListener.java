package com.study.vip.mall.mq;

import com.alibaba.fastjson.JSON;
import com.study.vip.mall.order.service.OrderService;
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
import java.util.Map;

@Component
@RocketMQMessageListener(topic = "refundstatus", consumerGroup = "refundstatus-group")
public class RefundStatusResultListener implements RocketMQListener, RocketMQPushConsumerLifecycleListener {

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

    /***
     * 消息监听
     * @param consumer
     */
    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                try {
                    for (MessageExt msg : msgs) {
                        String result = new String(msg.getBody(),"UTF-8");
                        //获取payId，修改订单状态
                        Map<String,String> refundStatusMap = JSON.parseObject(result,Map.class);

                        //退款申请成功
                        String out_trade_no = refundStatusMap.get("out_trade_no"); //订单号
                        String out_refund_no = refundStatusMap.get("out_refund_no"); //订单号
                        if(refundStatusMap.get("return_code").equals("SUCCESS") && refundStatusMap.get("result_code").equals("SUCCESS")){
                            orderService.updateRefundStatus(out_trade_no,out_refund_no);
                        }else{
                            //退款失败,人工处理
                            orderService.updateRefundFailStatus(out_refund_no);
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
