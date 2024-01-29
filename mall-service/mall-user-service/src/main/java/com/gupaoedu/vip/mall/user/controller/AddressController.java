package com.study.vip.mall.user.controller;

import com.study.mall.util.RespResult;
import com.study.vip.mall.user.model.Address;
import com.study.vip.mall.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/address")
@CrossOrigin
public class AddressController {

    @Autowired
    private AddressService addressService;

    /***
     * 用户地址列表查询
     * @return
     */
    @GetMapping(value = "/list")
    public RespResult<List<Address>> list(){
        String userName = "gp";
        List<Address> addresses = addressService.list(userName);
        return RespResult.ok(addresses);
    }
}
