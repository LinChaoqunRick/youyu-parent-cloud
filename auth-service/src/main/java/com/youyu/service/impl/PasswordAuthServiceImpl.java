package com.youyu.service.impl;

import com.youyu.entity.auth.UserFramework;
import com.youyu.entity.auth.AuthParamsEntity;
import com.youyu.mapper.UserFrameworkMapper;
import com.youyu.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Mr.M
 * @version 1.0
 * @description 账号密码认证
 * @date 2022/10/20 14:49
 */
@Slf4j
@Service("password_authService")
public class PasswordAuthServiceImpl implements AuthService {

    private final String emailRegex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    @Resource
    private UserFrameworkMapper userFrameworkMapper;

    @Resource
    PasswordEncoder passwordEncoder;

    @Override
    public UserFramework execute(AuthParamsEntity authParamsEntity) {
        // 1. 获取账号
        String username = authParamsEntity.getUsername();

        // 2. 根据账号去数据库中查询是否存在
        UserFramework user = null;
        if (username.matches(emailRegex)) { // 如果是邮箱登录
            user = userFrameworkMapper.getUserByEmail(username);
        } else { // 如果是手机号登录
            user = userFrameworkMapper.getUserByUsername(username);
        }

        // 3. 不存在抛异常
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 4. 校验密码
        // 4.1 获取用户输入的密码
        String passwordForm = authParamsEntity.getPassword();
        // 4.2 获取数据库中存储的密码
        String passwordDb = user.getPassword();
        // 4.3 比较密码
        boolean matches = passwordEncoder.matches(passwordForm, passwordDb);
        // 4.4 不匹配，抛异常
        if (!matches) {
            throw new RuntimeException("用户名或密码错误");
        }
        return user;
    }
}
