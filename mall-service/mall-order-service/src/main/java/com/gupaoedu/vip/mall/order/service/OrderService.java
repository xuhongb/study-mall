package com.study.vip.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.vip.mall.order.model.Order;
import com.study.vip.mall.order.model.OrderRefund;

public interface OrderService extends IService<Order> {
    /***
     * 退款申请失败，修改退款记录状态
     * @param out_refund_no
     */
    void updateRefundFailStatus(String out_refund_no);
    /****
     * 退款申请成功
     * @param out_trade_no：订单号
     * @param out_refund_no：退款记录订单号
     */
    void updateRefundStatus(String out_trade_no,String out_refund_no);

    /**
     * 退款
     */
    int refund(OrderRefund orderRefund);

    /**
     *添加订单
     */
    Boolean add(Order order);

    /**
     * 支付成功修改状态
     */
    int updateAfterPayStatus(String id);
}
