package com.youyu.service.mail.impl;

import com.youyu.entity.LoginUser;
import com.youyu.entity.auth.UserFramework;
import com.youyu.entity.auth.AuthParamsEntity;
import com.youyu.mapper.UserFrameworkMapper;
import com.youyu.service.AuthService;
import com.youyu.utils.BeanTransformUtils;
import com.youyu.utils.RedisCache;
import com.youyu.utils.SecurityUtils;
import com.youyu.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    ApplicationContext applicationContext;

    @Resource
    private RedisCache redisCache;

    @Resource
    private UserFrameworkMapper userFrameworkMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpServletRequest request = WebUtils.getRequest();
        if (request == null) return null;

        LoginUser loginUser;
        AuthParamsEntity authParamsEntity;

        try {
            authParamsEntity = BeanTransformUtils.requestParamsMapToBean(request.getParameterMap(), AuthParamsEntity.class);
        } catch (Exception e) {
            log.error("认证请求数据格式不对：{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        // 获取认证类型，beanName就是 认证类型 + 后缀，例如 password + _authService = password_authService
        String authType = authParamsEntity.getAuthType() != null ? authParamsEntity.getAuthType() : "password";
        AuthService authService;
        try {
            // 根据认证类型，从Spring容器中取出对应的bean
            authService = applicationContext.getBean(authType + "_authService", AuthService.class);
        } catch (Exception e) {
            if (e instanceof NoSuchBeanDefinitionException) {
                throw new RuntimeException("无效的authType：" + authType);
            }
            throw new RuntimeException(e);
        }

        String clientId = SecurityUtils.getAuthentication().getName();
        loginUser = authService.execute(authParamsEntity, clientId);
        // 存入redis，便于资源服务器解析jwt获取authorities信息
        redisCache.setCacheObject(clientId + ":" + loginUser.getUser().getId(), loginUser);
        return loginUser;
    }
}
