package com.study.vip.mall.user.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
//MyBatisPlus表映射注解
@TableName(value = "user_info")
public class UserInfo implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private String username;
    private String password;
    private String phone;
    private String name;
    private Integer points;
    private String roles;
}
