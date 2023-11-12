package com.youyu.utils;

import com.youyu.entity.LoginUser;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

public class SecurityUtils {

    public static LoginUser getLoginUser() {
        if (getAuthentication().getPrincipal() == "anonymousUser") {
            return null;
        }
        return (LoginUser) getAuthentication().getPrincipal();
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static Boolean isAdmin() {
        Long id = getLoginUser().getUser().getId();
        return id != null && 10000L == id;
    }

    public static Long getUserId() {
        if (Objects.isNull(getLoginUser())) {
            return null;
        }
        return getLoginUser().getUser().getId();
    }

    public static boolean isContentUser(Long userId) {
        if (Objects.equals(getUserId(), userId)) {
            return true;
        } else {
            throw new SystemException(ResultCode.FORBIDDEN);
        }
    }

    public static boolean isContextUser(Long userId) {
        return Objects.equals(getUserId(), userId);
    }

    public static void authContextUser(Long userId) {
        if (!isContextUser(userId)) {
            throw new SystemException(ResultCode.FORBIDDEN);
        }
    }

    public static String getAuthorizationToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization)) {
            return authorization.split(" ")[1];
        } else {
            return null;
        }
    }
}
