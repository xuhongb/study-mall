package com.study.vip.mall.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.vip.mall.user.mapper.UserInfoMapper;
import com.study.vip.mall.user.model.UserInfo;
import com.study.vip.mall.user.service.UserInfoService;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
}
