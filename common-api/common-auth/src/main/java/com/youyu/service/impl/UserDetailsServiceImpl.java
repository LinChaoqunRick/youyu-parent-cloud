package com.youyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youyu.entity.LoginUser;
import com.youyu.entity.UserFramework;
import com.youyu.mapper.MenuMapper;
import com.youyu.mapper.UserFrameworkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private String emailRegex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private UserFrameworkMapper userFrameworkMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<UserFramework> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        UserFramework user;
        // 查询用户信息
        if (username.matches(emailRegex)) { // 如果是邮箱登录
            user = userFrameworkMapper.getUserByEmail(username);
        } else { // 如果是手机号登录
            user = userFrameworkMapper.getUserByUsername(username);
        }


        // 如果没有查询到用户就抛出异常
        if (Objects.isNull(user)) {
            throw new RuntimeException("用户名或密码错误");
        }
        // TODO 查询对应权限信息
        List<String> permission = menuMapper.selectPermsByUserId(user.getId());
        // 把数据封装成UserDetails返回
        return new LoginUser(user, permission);
    }
}
