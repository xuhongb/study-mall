package com.study.vip.mall.pay;

import com.study.vip.mall.order.model.OrderRefund;
import com.study.vip.mall.util.IPUtils;
import com.study.mall.util.Signature;
import com.study.vip.mall.order.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Component
public class WeixinPayParam {

    @Autowired
    private Signature signature;

    /****
     * 微信支付参数封装
     * 对参数进行签名
     * 对整体参数进行加密
     * @return
     */
    public String weixinParam(Order order, HttpServletRequest request) throws Exception {
        //预支付下单定义Map封装参数
        Map<String,String> dataMap = new HashMap<String,String>();
        dataMap.put("body", "商城订单-"+order.getId());
        dataMap.put("out_trade_no", order.getId());
        dataMap.put("device_info", "PC");
        dataMap.put("fee_type", "CNY");
        //dataMap.put("total_fee", String.valueOf(order.getMoneys()*100));
        dataMap.put("total_fee", "1"); //1分钱测试
        dataMap.put("spbill_create_ip", IPUtils.getIpAddr(request));
        //消息异步通知
        dataMap.put("notify_url", "http://www.example.com/wxpay/notify");//http://2cw4969042.wicp.vip:25082/wx/result
        dataMap.put("trade_type", "NATIVE");  // 此处指定为扫码支付
        //生成签名，并且参数加密 treeMap->MD5->JSON->AES
        return signature.security(dataMap);
    }

    /****
     * 微信退款参数封装
     * 对参数进行签名
     * 对整体参数进行加密
     * @return
     */
    public String weixinRefundParam(OrderRefund orderRefund) throws Exception {
        //定义Map封装参数
        Map<String,String> dataMap = new HashMap<String,String>();
        dataMap.put("out_trade_no", orderRefund.getOrderNo());//订单号
        dataMap.put("out_refund_no", orderRefund.getId());//退款订单号
        //dataMap.put("total_fee", String.valueOf(order.getMoneys()));
        dataMap.put("total_fee", "1"); //1分钱测试 支付金额
        //退款金额
        //dataMap.put("refund_fee", String.valueOf(order.getMoneys()));
        dataMap.put("refund_fee", "1");//1分钱测试
        dataMap.put("notify_url", "http://2cw4969042.wicp.vip:25082/wx/refund/result");
        //生成签名，并且参数加密
        return signature.security(dataMap);
    }
}
