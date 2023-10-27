package com.youyu.entity.auth;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr.M
 * @version 1.0
 * @description 统一认证入口后统一提交的数据
 * @date 2022/9/29 10:56
 */
@Data
public class AuthParamsEntity {
    private String client_id; // 客户端id
    private String client_secret; // 客户端密码
    private String username; // 用户名
    private String password; // 密码
    private String refresh_token; // refresh_token
    private String smsCode; // 短信验证码
    private String authType; // 认证的类型   password:用户名密码模式类型    sms:短信模式类型

}
