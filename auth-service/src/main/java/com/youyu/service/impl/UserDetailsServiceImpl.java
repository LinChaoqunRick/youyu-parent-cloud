package com.youyu.service.impl;

import com.alibaba.fastjson.JSON;
import com.youyu.entity.LoginUser;
import com.youyu.entity.UserFramework;
import com.youyu.entity.auth.AuthParamsEntity;
import com.youyu.mapper.MenuMapper;
import com.youyu.mapper.UserFrameworkMapper;
import com.youyu.service.AuthService;
import com.youyu.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private MenuMapper menuMapper;

    @Resource
    ApplicationContext applicationContext;

    @Resource
    private RedisCache redisCache;

    @Resource
    private UserFrameworkMapper userFrameworkMapper;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AuthParamsEntity authParamsEntity = null;
        UserFramework user = null;
        try {
            authParamsEntity = JSON.parseObject(s, AuthParamsEntity.class);
            String authType = authParamsEntity.getAuthType(); // 获取认证类型，beanName就是 认证类型 + 后缀，例如 password + _authservice = password_authservice
            AuthService authService = applicationContext.getBean(authType + "_authService", AuthService.class); // 根据认证类型，从Spring容器中取出对应的bean
            user = authService.execute(authParamsEntity);
        } catch (Exception e) {
            user = userFrameworkMapper.getUserByUsername(s);
        }

        // 查询对应权限信息
        List<String> permission = menuMapper.selectPermsByUserId(user.getId());

        // 把数据封装成UserDetails返回
        LoginUser loginUser = new LoginUser(user, permission);

        // 把完整的用户信息存入redis userId作为key
        redisCache.setCacheObject("user:" + user.getId(), loginUser);

        return loginUser;
    }
}
