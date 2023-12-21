package com.youyu.interceptor;

import com.youyu.utils.RequestUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component
public class OAuth2FeignRequestInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";
    private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";

    @Override
    public void apply(RequestTemplate template) {
        // 服务间调用携带jwt令牌
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
            OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
            template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, details.getTokenValue()));
        }

        // 传递真实调用者ip地址信息
        HttpServletRequest request = RequestUtils.getRequest();
        String x_forward_for = request.getHeader(X_FORWARDED_FOR);
        if (x_forward_for != null) {
            template.header(X_FORWARDED_FOR, x_forward_for);
        }
    }
}
