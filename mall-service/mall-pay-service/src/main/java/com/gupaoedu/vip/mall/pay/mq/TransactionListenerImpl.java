package com.study.vip.mall.pay.mq;

import com.alibaba.fastjson.JSON;
import com.study.vip.mall.pay.model.PayLog;
import com.study.vip.mall.pay.service.PayLogService;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RocketMQTransactionListener(txProducerGroup = "rocket")
public class TransactionListenerImpl implements RocketMQLocalTransactionListener {
    @Autowired
    private PayLogService payLogService;

    /***
     * 当向RocketMQ的Broker发送Half消息成功之后，调用该方法
     * 发送prepare消息成功后回调该方法用于执行本地事务
     * @param message:回传的消息，利用transactionId即可获取到该消息的唯一Id
     * @param arg:调用send方法时传递的参数，当send时候若有额外的参数可以传递到send方法中，这里能获取到
     * @return
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {
        try {
            //================本地事务操作开始=====================================
            //将o转成PayLog
            String result = new String((byte[]) message.getPayload(),"UTF-8");
            PayLog payLog = JSON.parseObject(result,PayLog.class);
            payLogService.add(payLog);
            //================本地事务操作结束=====================================
        } catch (Exception e) {
            //异常,消息回滚
            e.printStackTrace();
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return RocketMQLocalTransactionState.COMMIT;
    }

    /***
     * 消息回查
     * @param message
     * @return
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        return RocketMQLocalTransactionState.COMMIT;
    }
}
