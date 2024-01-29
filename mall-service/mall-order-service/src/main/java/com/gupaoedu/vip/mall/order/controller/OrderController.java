package com.study.vip.mall.order.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.study.mall.util.RespCode;
import com.study.mall.util.RespResult;
import com.study.vip.mall.order.model.Order;
import com.study.vip.mall.order.model.OrderRefund;
import com.study.vip.mall.order.service.OrderService;
import com.study.vip.mall.pay.WeixinPayParam;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping(value = "/order")
@CrossOrigin
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private WeixinPayParam weixinPayParam;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /***
     * 取消订单
     */
    @PutMapping(value = "/refund/{id}")
    public RespResult refund(@PathVariable(value = "id")String id,HttpServletRequest request) throws Exception {
        String userName = "gp";

        //查询商品信息
        Order order = orderService.getById(id);

        //已支付，待发货，才允许取消订单
        if(order.getOrderStatus().intValue()==1 && order.getPayStatus().intValue()==1){
            //退款记录
            OrderRefund orderRefund = new OrderRefund(
                    IdWorker.getIdStr(),
                    order.getId(),
                    0,//0 整个订单退款，1 单个明细退款
                    null,
                    userName,
                    0,//状态，0：申请退款，1：退款成功，2：退款失败
                    new Date(),
                    order.getMoneys()  //退款金额
            );

            //发送事务消息[退款加密信息]
            Message message = MessageBuilder.withPayload(weixinPayParam.weixinRefundParam(orderRefund)).build();
            TransactionSendResult transactionSendResult = rocketMQTemplate.sendMessageInTransaction("refundtx", "refund", message, orderRefund);
            if(transactionSendResult.getSendStatus()== SendStatus.SEND_OK){
                return RespResult.error("申请退款成功，等待退款！");
            }
            return RespResult.error("不符合取消订单条件，无法退货！");
        }
        return RespResult.error("订单已发货，或无法退货！");
    }
    /***
     * 添加订单
     */
    @PostMapping
    public RespResult add(@RequestBody Order order, HttpServletRequest request) throws Exception {
        String userName = "gp";
        order.setUsername(userName);
        order.setCreateTime(new Date());
        order.setUpdateTime(order.getCreateTime());
        order.setId(IdWorker.getIdStr());
        order.setOrderStatus(0);
        order.setPayStatus(0);
        order.setIsDelete(0);
        //添加订单
        Boolean bo = orderService.add(order);
        //支付信息封装
        if(bo){
            //加密字符
            String ciptext = weixinPayParam.weixinParam(order,request);
            return RespResult.ok(ciptext);
        }
        return RespResult.error(RespCode.SYSTEM_ERROR);
    }
//    /***
//     * 添加订单
//     */
//    @PostMapping
//    public RespResult add(@RequestBody Order order){
//        String userName = "gp";
//        order.setUsername(userName);
//        order.setCreateTime(new Date());
//        order.setUpdateTime(order.getCreateTime());
//        order.setId(IdWorker.getIdStr());
//        order.setOrderStatus(0);
//        order.setPayStatus(0);
//        order.setIsDelete(0);
//
//        //添加订单
//        Boolean bo = orderService.add(order);
//        return bo? RespResult.ok():RespResult.error(RespCode.SYSTEM_ERROR);
//    }
}
