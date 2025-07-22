package com.youyu.service.mail.impl;

import com.youyu.entity.LoginUser;
import com.youyu.entity.auth.UserFramework;
import com.youyu.entity.auth.AuthParamsEntity;
import com.youyu.mapper.MenuMapper;
import com.youyu.mapper.UserFrameworkMapper;
import com.youyu.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;
import java.util.Objects;

/**
 * @author Mr.M
 * @version 1.0
 * @description 账号密码认证
 * @date 2022/10/20 14:49
 */
@Slf4j
@Service("password_authService")
public class PasswordAuthServiceImpl implements AuthService {

    @Resource
    private UserFrameworkMapper userFrameworkMapper;

    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    private MenuMapper menuMapper;

    @Override
    public LoginUser execute(AuthParamsEntity authParamsEntity, String clientId) {
        String username = authParamsEntity.getUsername();
        UserFramework user = null;
        List<String> permission = null;
        String emailRegex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        if (username.matches(emailRegex)) { // 如果是邮箱登录
            user = userFrameworkMapper.getUserByEmail(username);
        } else { // 如果是手机号登录
            if (Objects.equals(clientId, "youyu-content")) {
                user = userFrameworkMapper.getUserByUsername(username);
            } else if (Objects.equals(clientId, "youyu-manage")) {
                user = userFrameworkMapper.getManageUserByUsername(username);
            }
        }
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        String passwordForm = authParamsEntity.getPassword();
        String passwordDb = user.getPassword();
        boolean matches = passwordEncoder.matches(passwordForm, passwordDb);
        if (!matches) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (Objects.equals(clientId, "youyu-content")) {
            permission = menuMapper.selectPermsByUserId(user.getId());
        } else if (Objects.equals(clientId, "youyu-manage")) {
            permission = menuMapper.selectManagePermsByUserId(user.getId());
        }
        return new LoginUser(user, permission);
    }
}
