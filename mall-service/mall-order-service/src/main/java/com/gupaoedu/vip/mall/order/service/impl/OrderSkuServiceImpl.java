package com.study.vip.mall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.vip.mall.order.mapper.OrderSkuMapper;
import com.study.vip.mall.order.model.OrderSku;
import com.study.vip.mall.order.service.OrderSkuService;
import org.springframework.stereotype.Service;

@Service
public class OrderSkuServiceImpl extends ServiceImpl<OrderSkuMapper, OrderSku> implements OrderSkuService {
}
