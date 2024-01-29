package com.study.vip.mall.pay.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.study.mall.util.*;
import com.study.vip.mall.pay.model.PayLog;

import com.study.vip.mall.pay.service.WeixinPayService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/wx")
@CrossOrigin
public class WeixinPayController {


    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private WeixinPayService weixinPayService;

    @Autowired
    private Signature signature;

    @Value("${payconfig.aes.skey}")
    private String skey;

    /***
     * 支付状态查询
     */
    @GetMapping(value = "/result/{outno}")
    public RespResult<PayLog> query(@PathVariable(value = "outno")String outno) throws Exception {
        PayLog payLog = weixinPayService.result(outno);
        return RespResult.ok(payLog);
    }

    /*****
     * 预下单
     */
    @GetMapping(value = "/pay")
    //public RespResult<Map> pay(@RequestParam Map<String,String> map) throws Exception {
    public RespResult<Map> pay(@RequestParam(value = "ciphertext") String ciphertext) throws Exception {
        //数据解析，并验签校验
        Map<String, String> map = signature.security(ciphertext);
        //1分钱测试
        if(map!=null){
            Map<String, String> resultMap = weixinPayService.preOrder(map);
            resultMap.put("orderNumber",map.get("out_trade_no"));
            resultMap.put("money",map.get("total_fee"));
            return RespResult.ok(resultMap);
        }
        return RespResult.error("支付系统繁忙，请稍后再试!");
    }

    /***
     * 记录支付结果
     * 执行事务消息发送 支付结果回调
     */
    @RequestMapping(value = "/result")
    public String result(HttpServletRequest request) throws Exception {
        //读取网络输入流
        ServletInputStream is = request.getInputStream();
        //定义接收输入流对象（输出流）
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        //缓冲区定义
        byte[] buffer = new byte[1024];
        int len =0;
        //将网络输入流读取到输出流中
        while ((len=is.read(buffer))!=-1){
            os.write(buffer,0,len);
        }

        //关闭资源
        os.close();
        is.close();
        //将支付结果的XML结构转换成Map结构
        String xmlResult = new String(os.toByteArray(),"utf-8");
        Map<String,String> responseMap = WXPayUtil.xmlToMap(xmlResult);
        //判断支付结果
        //记录日志
        int status = 7;//支付失败
        if(responseMap.get("return_code").equals(WXPayConstants.SUCCESS) && responseMap.get("result_code").equals(WXPayConstants.SUCCESS)){
            status=2;//已支付
        }
        PayLog payLog = new PayLog(responseMap.get("out_trade_no"),status,JSON.toJSONString(responseMap),responseMap.get("out_trade_no"),new Date());
        Message message = MessageBuilder.withPayload(JSON.toJSONString(payLog)).build();
        rocketMQTemplate.sendMessageInTransaction("rocket","log",message,null);

        //返回结果
        Map<String,String> resultMap = new HashMap<String,String>();
        resultMap.put("return_code","SUCCESS");
        resultMap.put("return_msg","OK");
        return WXPayUtil.mapToXml(resultMap);
        //记录日志
//        PayLog payLog = new PayLog(IdWorker.getIdStr(),1,"test","No001",new Date());
//        //构建消息
//        Message message = MessageBuilder.withPayload(JSON.toJSONString(payLog)).build();
//        rocketMQTemplate.sendMessageInTransaction("rocket","log",message,null);
//        return RespResult.ok();
    }

    /***
     * 申请退款状态  退款通知结果
     */
    @RequestMapping(value = "/refund/result")
    public String refund(HttpServletRequest request) throws Exception {
        //读取网络输入流
        ServletInputStream is = request.getInputStream();
        //定义接收输入流对象（输出流）
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        //缓冲区定义
        byte[] buffer = new byte[1024];
        int len =0;
        //将网络输入流读取到输出流中
        while ((len=is.read(buffer))!=-1){
            os.write(buffer,0,len);
        }

        //关闭资源
        os.close();
        is.close();
        //将支付结果的XML结构转换成Map结构
        String xmlResult = new String(os.toByteArray(),"utf-8");
        Map<String,String> responseMap = WXPayUtil.xmlToMap(xmlResult);

        //发送MQ消息，普通消息，非事务消息 此部分的消息未实现
        Message message = MessageBuilder.withPayload(JSON.toJSONString(responseMap)).build();
        rocketMQTemplate.send("lastrefundresult",message);
        //判断支付结果
        System.out.println("xmlResult:"+xmlResult);
        //获取退款信息（加密了-AES）
        String reqinfo = responseMap.get("req_info");
        String key = MD5.md5(skey);
        byte[] decode = AESUtil.encryptAndDecrypt(Base64Util.decode(reqinfo), key, 2);
        System.out.println("退款解密后的数据："+new String(decode, "UTF-8"));
        //返回结果
        Map<String,String> resultMap = new HashMap<String,String>();
        resultMap.put("return_code","SUCCESS");
        resultMap.put("return_msg","OK");
        return WXPayUtil.mapToXml(resultMap);
        //记录日志
//        PayLog payLog = new PayLog(IdWorker.getIdStr(),1,"test","No001",new Date());
//        //构建消息
//        Message message = MessageBuilder.withPayload(JSON.toJSONString(payLog)).build();
//        rocketMQTemplate.sendMessageInTransaction("rocket","log",message,null);
//        return RespResult.ok();
    }

}
