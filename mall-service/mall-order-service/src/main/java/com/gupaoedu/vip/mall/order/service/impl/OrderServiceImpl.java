package com.study.vip.mall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.mall.util.RespResult;
import com.study.vip.mall.cart.feign.CartFeign;
import com.study.vip.mall.cart.model.Cart;
import com.study.vip.mall.goods.feign.SkuFeign;
import com.study.vip.mall.order.mapper.OrderMapper;
import com.study.vip.mall.order.mapper.OrderRefundMapper;
import com.study.vip.mall.order.mapper.OrderSkuMapper;
import com.study.vip.mall.order.model.Order;
import com.study.vip.mall.order.model.OrderRefund;
import com.study.vip.mall.order.model.OrderSku;
import com.study.vip.mall.order.service.OrderService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.keyvalue.core.IterableConverter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    private OrderSkuMapper orderSkuMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private CartFeign cartFeign;

    @Autowired
    private OrderRefundMapper orderRefundMapper;

    /***
     * 退款申请失败，修改退款记录状态
     * @param out_refund_no
     */
    @Override
    public void updateRefundFailStatus(String out_refund_no) {
        OrderRefund orderRefund = new OrderRefund();
        orderRefund.setId(out_refund_no);
        orderRefund.setStatus(1);   //退款申请失败（微信自动退款失败）
        orderRefundMapper.updateById(orderRefund);
    }
    /***
     * 退款申请成功
     * @param out_trade_no：订单号
     * @param out_refund_no：退款记录订单号
     */
    @Override
    public void updateRefundStatus(String out_trade_no,String out_refund_no) {
        //订单状态更新
        Order order = new Order();
        order.setId(out_trade_no); //ID
        order.setOrderStatus(5); //退款申请成功
        orderMapper.updateById(order);

        //修改退款记录状态
        OrderRefund orderRefund = new OrderRefund();
        orderRefund.setId(out_refund_no);
        orderRefund.setStatus(2);   //退款申请成功，等待微信退款
        orderRefundMapper.updateById(orderRefund);
    }
    /**
     * 退款申请
     * @param orderRefund
     * @return
     */
    @Override
    public int refund(OrderRefund orderRefund) {
        //退款申请记录
        int icount = orderRefundMapper.insert(orderRefund);

        //订单状态变更
        Order order = new Order();
        order.setOrderStatus(4);            //申请退款
        //条件
        QueryWrapper<Order> queryWrapper = new QueryWrapper<Order>();
        queryWrapper.eq("id",orderRefund.getOrderNo());
        queryWrapper.eq("username",orderRefund.getUsername());
        //原来是已支付待发货状态
        queryWrapper.eq("order_status",1);  //待发货
        queryWrapper.eq("pay_status",1);    //已支付
        int mcount = orderMapper.update(order,queryWrapper);
        return icount;
    }

    /***
     * 添加订单
     * 作业：
     *      1)价格如何校验？
     */
    @GlobalTransactional
    @Override
    public Boolean add(Order order) {
        //1.查询购物车记录
        RespResult<List<Cart>> cartResp = cartFeign.list(order.getCartIds());
        List<Cart> carts = IterableConverter.toList(cartResp.getData());
        if(carts.size()==0){
            return false;
        }
        //2.库存递减   20000  成功
        skuFeign.decount(carts);

        //3.增加订单明细
        int totlNum = 0;    //商品个数
        int payMoney = 0;   //支付总金额
        for (Cart cart : carts) {
            //类型转换
            OrderSku orderSku = JSON.parseObject(JSON.toJSONString(cart), OrderSku.class);
            orderSku.setId(IdWorker.getIdStr());
            orderSku.setMoney(orderSku.getPrice()*orderSku.getNum());
            orderSku.setSkuId(cart.getSkuId());
            orderSku.setOrderId(order.getId());
            orderSkuMapper.insert(orderSku);

            //统计数据
            totlNum+=cart.getNum();
            payMoney+=orderSku.getMoney();
        }
        //4.增加订单
        order.setTotalNum(totlNum);
        order.setMoneys(payMoney);
        orderMapper.insert(order);

        //5.删除购物车记录
        cartFeign.delete(order.getCartIds());
        return true;
    }

    /***
     * 支付后，修改订单状态
     * @param id
     * @return
     */
    @Override
    public int updateAfterPayStatus(String id) {
        Order order = new Order();
        order.setId(id);
        order.setPayStatus(1);  //已支付
        order.setOrderStatus(1); //待发货

        QueryWrapper<Order> queryWrapper = new QueryWrapper<Order>();
        queryWrapper.eq("id",id);
        queryWrapper.eq("order_status",0);  //未完成
        queryWrapper.eq("pay_status",0);    //未支付
        return orderMapper.update(order,queryWrapper);
    }
}
