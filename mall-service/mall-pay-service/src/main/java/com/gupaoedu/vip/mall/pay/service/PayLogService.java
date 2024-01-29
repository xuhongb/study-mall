package com.study.vip.mall.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.vip.mall.pay.model.PayLog;

public interface PayLogService extends IService<PayLog> {
    void  add(PayLog payLog);
}
