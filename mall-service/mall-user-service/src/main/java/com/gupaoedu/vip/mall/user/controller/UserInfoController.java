package com.study.vip.mall.user.controller;

import com.study.mall.util.JwtToken;
import com.study.mall.util.MD5;
import com.study.mall.util.RespResult;
import com.study.vip.mall.user.model.UserInfo;
import com.study.vip.mall.user.service.UserInfoService;
import com.study.vip.mall.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/user/info")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 登录
     * @param username
     * @param pwd
     * @return
     */
    @PostMapping("/login")
    public RespResult<String> login(@RequestParam(value = "username")String username,
                                    @RequestParam(value = "pwd")String pwd,
                                    HttpServletRequest request) throws Exception {
        //登录
        UserInfo userInfo = userInfoService.getById(username);
        if(userInfo!=null){
            //匹配密码是否一致
            if(userInfo.getPassword().equals(pwd)){
                //封装用户信息实现加密
                Map<String,Object> dataMap = new HashMap<String,Object>();
                dataMap.put("username",userInfo.getUsername());
                dataMap.put("name",userInfo.getName());
                dataMap.put("roles",userInfo.getRoles());
                //获取客户端IP
                String ip = IPUtils.getIpAddr(request);
                dataMap.put("ip", MD5.md5(ip));
                //创建令牌
                String token = JwtToken.createToken(dataMap);
                return RespResult.ok(token);
            }
            //账号密码不匹配
            return RespResult.error("账号或者密码错误");
        }
        return RespResult.error("账号不存在");
    }

}
