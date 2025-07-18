package com.youyu.utils;

import cn.hutool.core.convert.Convert;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

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

    public static Long getUserId() {
        Map<String, Object> tokenAttributes = getTokenAttributes();
        if (tokenAttributes != null) {
            return Convert.toLong(tokenAttributes.get("user_id"));
        }
        return null;
    }

    public static String getUsername() {
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

    public static boolean isContextUser(Long userId) {
        return Objects.equals(getUserId(), userId);
    }

    /**
     * 水平越权检验
     *
     * @param userId 用户id，将与认证的用户id进行比较
     */
    public static void authAuthorizationUser(Long userId) {
        if (!isContextUser(userId)) {
            throw new SystemException(ResultCode.FORBIDDEN);
        }
    }

    /**
     * 获取请求中的X-User-Id
     *
     * @return
     */
    public static Long getRequestAuthenticateUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String X_User_ID = request.getHeader("X-User-Id");
        if (Objects.nonNull(X_User_ID)) {
            return Long.valueOf(X_User_ID);
        } else {
            return null;
        }
    }
}
