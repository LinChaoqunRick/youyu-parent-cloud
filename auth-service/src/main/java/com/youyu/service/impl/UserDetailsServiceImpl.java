package com.youyu.service.impl;

import com.alibaba.fastjson.JSON;
import com.youyu.entity.LoginUser;
import com.youyu.entity.UserFramework;
import com.youyu.entity.auth.AuthParamsEntity;
import com.youyu.mapper.MenuMapper;
import com.youyu.mapper.UserFrameworkMapper;
import com.youyu.service.AuthService;
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

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AuthParamsEntity authParamsEntity = null;
        try {
            authParamsEntity = JSON.parseObject(s, AuthParamsEntity.class);
        } catch (Exception e) {
            log.error("认证请求数据格式不对：{}", s);
            throw new RuntimeException("认证请求数据格式不对");
        }

        // 获取认证类型，beanName就是 认证类型 + 后缀，例如 password + _authservice = password_authservice
        String authType = authParamsEntity.getAuthType();
        // 根据认证类型，从Spring容器中取出对应的bean
        AuthService authService = applicationContext.getBean(authType + "_authService", AuthService.class);
        UserFramework user = authService.execute(authParamsEntity);

        // 查询对应权限信息
        List<String> permission = menuMapper.selectPermsByUserId(user.getId());
        // 把数据封装成UserDetails返回
        return new LoginUser(user, permission);
    }
}
