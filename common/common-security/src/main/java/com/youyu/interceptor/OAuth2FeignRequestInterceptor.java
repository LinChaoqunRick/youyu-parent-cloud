package com.youyu.interceptor;

import com.youyu.utils.RequestUtils;
import com.youyu.utils.SecurityUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class OAuth2FeignRequestInterceptor implements RequestInterceptor {

    private static final String X_USER_ID = "X-User-Id";
    private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";

    @Override
    public void apply(RequestTemplate template) {
        // 服务间调用携带AuthorizationUserId
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 判断是否为已登录状态
        if (authentication != null && authentication.isAuthenticated()) {
            // 自定义工具类从 Authentication 中取用户信息
            Long userId = SecurityUtils.getRequestAuthenticateUserId();
            if (null != userId) {
                template.header(X_USER_ID, String.valueOf(userId));
            }
        }

        // 传递真实调用者ip地址信息
        HttpServletRequest request = RequestUtils.getRequest();
        if (request != null) {
            String x_forward_for = request.getHeader(X_FORWARDED_FOR);
            template.header(X_FORWARDED_FOR, x_forward_for);
        }
    }
}
