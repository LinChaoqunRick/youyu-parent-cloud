package com.youyu.utils;

import com.youyu.entity.LoginUser;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

public class SecurityUtils {

    public static LoginUser getLoginUser() {
        Object principal = getAuthentication().getPrincipal();
        // anonymousUser: 未登陆用户访问接口，principal instanceof User: 访问了登陆接口
        if (principal == "anonymousUser" || principal instanceof User) {
            return null;
        }
        return (LoginUser) principal;
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static Boolean isAdmin() {
        Long id = Objects.requireNonNull(getLoginUser()).getUser().getId();
        return id != null && 10000L == id;
    }

    public static Long getUserId() {
        if (Objects.isNull(getLoginUser())) {
            return null;
        }
        return getLoginUser().getUser().getId();
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
     * 获取请求中的JWT Token
     *
     * @return
     */
    public static String getAuthorizationToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization)) {
            return authorization.split(" ")[1];
        } else {
            return null;
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
