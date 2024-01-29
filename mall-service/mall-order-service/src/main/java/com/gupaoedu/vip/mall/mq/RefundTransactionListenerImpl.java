package com.study.vip.mall.mq;

import com.study.vip.mall.order.mapper.OrderRefundMapper;
import com.study.vip.mall.order.model.OrderRefund;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RocketMQTransactionListener(txProducerGroup = "refundtx")
public class RefundTransactionListenerImpl implements RocketMQLocalTransactionListener {
    @Autowired
    private OrderRefundMapper orderRefundMapper;

    /***
     * 变更订单状态，记录退款申请信息
     * 发送prepare消息成功后回调该方法用于执行本地事务
     * @param message:回传的消息，利用transactionId即可获取到该消息的唯一Id
     * @param o:调用send方法时传递的参数，当send时候若有额外的参数可以传递到send方法中，这里能获取到
     * @return
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {
        try {
            //================本地事务操作开始=====================================
            System.out.println("变更订单状态，记录退款申请信息");
            //修改本地状态
            //退款申请记录
            OrderRefund orderRefund = (OrderRefund) arg;
            orderRefundMapper.deleteById(orderRefund.getId());
            int count = orderRefundMapper.insert(orderRefund);
            System.out.println("变更订单状态，记录退款申请信息"+count);
            //如果申请退款失败，则回滚half消息
            if(count<=0){
                return RocketMQLocalTransactionState.ROLLBACK;
            }
            //================本地事务操作结束=====================================
        } catch (Exception e) {
            //异常,消息回滚
            e.printStackTrace();
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return RocketMQLocalTransactionState.COMMIT;
    }

    /**
     * 消息回查
     * @param message
     * @return
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        return RocketMQLocalTransactionState.COMMIT;
    }
}
