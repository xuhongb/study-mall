package com.study.vip.mall.pay.mq;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.study.vip.mall.pay.mapper.RefundLogMapper;
import com.study.vip.mall.pay.model.RefundLog;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
@RocketMQTransactionListener(txProducerGroup = "refundstatustx")
public class RefundStatusTransactionListenerImpl implements RocketMQLocalTransactionListener {

    @Autowired
    private RefundLogMapper refundLogMapper;

    /***
     * 发送prepare消息成功后回调该方法用于执行本地事务
     * @param message:回传的消息，利用transactionId即可获取到该消息的唯一Id
     * @param o:调用send方法时传递的参数，当send时候若有额外的参数可以传递到send方法中，这里能获取到
     * @return
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        try {
            //================本地事务操作开始=====================================
            //将o转成Map
            Map<String,String> resultMap = (Map<String, String>) o;
            //添加退款日志记录
            RefundLog refundLog = new RefundLog(
                    IdWorker.getIdStr(),
                    resultMap.get("out_trade_no"),  //原订单号
                    resultMap.get("out_trade_no"),  //退款订单号（order_refund的id）
                    Integer.valueOf(resultMap.get("refund_fee")),   //退款金额
                    new Date()
            );
            int count = refundLogMapper.insert(refundLog);
            //================本地事务操作结束=====================================
        } catch (Exception e) {
            //异常,消息回滚
            e.printStackTrace();
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return RocketMQLocalTransactionState.UNKNOWN;
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
