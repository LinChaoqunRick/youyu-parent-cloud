package com.youyu.utils;

import com.youyu.entity.LoginUser;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
            throw new SystemException(ResultCode.NO_OPERATOR_AUTH);
        }
    }

    public static boolean isContextUser(Long userId) {
        return Objects.equals(getUserId(), userId);
    }

    public static void authContextUser(Long userId) {
        if (!isContextUser(userId)) {
            throw new SystemException(ResultCode.NO_OPERATOR_AUTH);
        }
    }
}
