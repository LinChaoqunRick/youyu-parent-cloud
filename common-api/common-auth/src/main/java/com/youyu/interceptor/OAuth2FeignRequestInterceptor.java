package com.youyu.interceptor;

import com.youyu.utils.RequestUtils;
import com.youyu.utils.SecurityUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class OAuth2FeignRequestInterceptor implements RequestInterceptor {

    private static final String AUTHENTICATED_USERID = "AuthenticateUserId";
    private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";

    @Override
    public void apply(RequestTemplate template) {
        // 服务间调用携带AuthorizationUserId
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
            template.header(AUTHENTICATED_USERID, String.valueOf(SecurityUtils.getRequestAuthenticateUserId()));
        }

        // 传递真实调用者ip地址信息
        HttpServletRequest request = RequestUtils.getRequest();
        if (request != null) {
            String x_forward_for = request.getHeader(X_FORWARDED_FOR);
            template.header(X_FORWARDED_FOR, x_forward_for);
        }
    }
}
