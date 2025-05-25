package com.youyu.service.mail.impl;

import com.youyu.entity.LoginUser;
import com.youyu.entity.auth.UserFramework;
import com.youyu.entity.auth.AuthParamsEntity;
import com.youyu.mapper.MenuMapper;
import com.youyu.mapper.UserFrameworkMapper;
import com.youyu.service.AuthService;
import com.youyu.utils.BeanTransformUtils;
import com.youyu.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String[]> map = request.getParameterMap();
        UserFramework user = null;
        AuthParamsEntity authParamsEntity = null;
        try {
            authParamsEntity = BeanTransformUtils.requestParamsMapToBean(map, AuthParamsEntity.class);
        } catch (Exception e) {
            log.error("认证请求数据格式不对：{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        if (Objects.nonNull(authParamsEntity.getRefresh_token())) { // 如果是refresh_token
            user = userFrameworkMapper.getUserByUsername(username);
        } else {
            String authType = authParamsEntity.getAuthType() != null ? authParamsEntity.getAuthType() : "password"; // 获取认证类型，beanName就是 认证类型 + 后缀，例如 password + _authService = password_authService
            AuthService authService = null;
            try {
                authService = applicationContext.getBean(authType + "_authService", AuthService.class); // 根据认证类型，从Spring容器中取出对应的bean
            } catch (Exception e) {
                if (e instanceof NoSuchBeanDefinitionException) {
                    throw new RuntimeException("无效的authType：" + authType);
                }
            }
            assert authService != null;
            user = authService.execute(authParamsEntity);
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
