package com.youyu.oauth2.oidc;

import cn.hutool.core.bean.BeanUtil;
import com.youyu.entity.auth.UserFramework;
import com.youyu.mapper.UserFrameworkMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 自定义 OIDC 用户信息服务
 *
 * @author Ray Hao
 * @since 3.1.0
 */
@Service
@Slf4j
public class CustomOidcUserInfoService {

    @Resource
    private UserFrameworkMapper userFrameworkMapper;

    public CustomOidcUserInfo loadUserByUsername(String username) {
        UserFramework user = null;
        try {
            user = userFrameworkMapper.getUserByUsername(username);
            if (user == null) {
                return null;
            }
            return new CustomOidcUserInfo(BeanUtil.beanToMap(user));
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return null;
        }
    }

}
