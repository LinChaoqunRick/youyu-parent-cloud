package com.youyu.utils;

import cn.hutool.core.convert.Convert;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;
import java.util.Objects;

public class SecurityUtils {

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static Map<String, Object> getTokenAttributes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            return jwtAuthenticationToken.getTokenAttributes();
        }
        return null;
    }

    /**
     * 获取当前认证用户id
     * @return 用户id
     */
    public static Long getUserId() {
        Map<String, Object> tokenAttributes = getTokenAttributes();
        if (tokenAttributes != null) {
            return Convert.toLong(tokenAttributes.get("user_id"));
        }
        return null;
    }

    /**
     * 用户已登录，获取clientId
     * @return 客户端id
     */
    public static String getJwtClientId() {
        Authentication authentication = getAuthentication();
        if (authentication instanceof JwtAuthenticationToken token) {
            Jwt jwt = token.getToken();
            return jwt.getAudience().get(0); // 取第一个作为 client_id
        }
        return null;
    }
    /**
     * 用户已登录，获取clientId
     * @return 客户端id
     */
    public static String getClientId() {
        Authentication authentication = getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * 水平越权检验，
     *
     * @param userId 用户id，将与认证的用户id进行比较
     */
    public static boolean isAuthorizationUser(Long userId) {
        return Objects.equals(getUserId(), userId);
    }

    /**
     * 水平越权检验，无权限错误
     * @param userId 用户id，将与认证的用户id进行比较
     */
    public static void authAuthorizationUser(Long userId) {
        if (!isAuthorizationUser(userId)) {
            throw new SystemException(ResultCode.FORBIDDEN);
        }
    }
}
