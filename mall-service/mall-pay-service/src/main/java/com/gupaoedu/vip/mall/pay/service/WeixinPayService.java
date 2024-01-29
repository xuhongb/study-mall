package com.study.vip.mall.pay.service;

import com.study.vip.mall.pay.model.PayLog;

import java.util.Map;

public interface WeixinPayService {
    /***
     * 退款
     * @param map
     * @return
     */
    Map<String, String>  refund(Map<String, String> map) throws Exception;
    /***
     * 统一下单，获取支付二维码
     * 预支付订单创建方法-获取支付地址
     */
    Map<String,String> preOrder(Map<String,String> dataMap) throws Exception;

    /***
     * 支付结果查询
     * @param outno
     * @return
     */
    PayLog result(String outno) throws Exception;
}
