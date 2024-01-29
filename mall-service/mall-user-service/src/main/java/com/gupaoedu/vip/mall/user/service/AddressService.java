package com.study.vip.mall.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.vip.mall.user.model.Address;

import java.util.List;

public interface AddressService extends IService<Address> {
    /**
     * 查询用户收件地址列表
     */
    public List<Address> list(String userName);
}
