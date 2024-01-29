package com.study.vip.mall.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.wxpay.sdk.WXPay;
import com.study.vip.mall.pay.mapper.PayLogMapper;
import com.study.vip.mall.pay.model.PayLog;
import com.study.vip.mall.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {
    @Autowired
    private WXPay wxPay;

    @Autowired
    private PayLogMapper payLogMapper;

    /**
     * 申请退款
     * @param dataMap
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, String> refund(Map<String, String> dataMap) throws Exception {
        return wxPay.refund(dataMap);
    }

    /***
     * 统一下单，获取支付二维码
     */
    @Override
    public Map<String, String> preOrder(Map<String, String> dataMap) throws Exception {
        Map<String, String> resp = wxPay.unifiedOrder(dataMap);
        return resp;
    }

    /***
     * 支付结果查询
     * @param outno 订单编号
     * @return
     */
    @Override
    public PayLog result(String outno) throws Exception {
        //查询数据库
        QueryWrapper<PayLog> queryWrapper = new QueryWrapper<PayLog>();
        queryWrapper.eq("pay_id",outno);
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("limit 1");
        PayLog payLog = payLogMapper.selectOne(queryWrapper);
        if(payLog==null){
            //微信服务器查询
            payLog = new PayLog();
            Map<String,String> queryMap = new HashMap<String,String>();
            queryMap.put("out_trade_no",outno);
            Map<String, String> resultMap = wxPay.orderQuery(queryMap);

            //交易状态
            int state = tradeState(resultMap.get("trade_state"));
            payLog.setStatus(state);
            payLog.setPayId(outno);
            //支付结果(日志记录时间)
            payLog.setCreateTime(new Date());
            payLog.setId(IdWorker.getIdStr());
            payLog.setContent(JSON.toJSONString(resultMap));

            //状态不可逆转：已支付、转入退款、已关闭、已撤销、支付失败
            if(state==2 || state==3 || state==4 || state==5 || state==7){
                //添加到数据库
                payLogMapper.insert(payLog);
            }
        }
        return payLog;
    }

    /***
     * 支付状态
     * @param tradeState
     * @return
     */
    public int tradeState(String tradeState){
        int state = 1;
        switch (tradeState){
            case "NOTPAY":  //未支付
                state = 1;
                break;
            case "SUCCESS":
                state = 2;  //已支付
                break;
            case "REFUND":
                state = 3;  //转入退款
                break;
            case "CLOSED":
                state = 4;  //已关闭
                break;
            case "REVOKED":
                state = 5;  //已撤销
                break;
            case "USERPAYING":
                state = 6;  //用户支付中
                break;
            case "PAYERROR":
                state = 7;  //支付失败
                break;
            default:
                state=1;
        }
        return state;
    }
}
