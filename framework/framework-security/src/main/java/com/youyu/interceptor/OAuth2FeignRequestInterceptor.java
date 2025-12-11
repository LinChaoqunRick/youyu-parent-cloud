package com.youyu.interceptor;

import com.youyu.utils.RequestUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class OAuth2FeignRequestInterceptor implements RequestInterceptor {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_X_FORWARDED_FOR = "X-FORWARDED-FOR";

    @Override
    public void apply(RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtToken && jwtToken.isAuthenticated()) {
            // 设置 Bearer Token
            String token = jwtToken.getToken().getTokenValue();
            template.header(HEADER_AUTHORIZATION, "Bearer " + token);
        }

        // 传递 X-FORWARDED-FOR 头
        HttpServletRequest request = RequestUtils.getRequest(); // 你自定义的工具类
        if (request != null) {
            String forwardedFor = request.getHeader(HEADER_X_FORWARDED_FOR);
            if (StringUtils.hasText(forwardedFor)) {
                template.header(HEADER_X_FORWARDED_FOR, forwardedFor);
            } else {
                // 如果没有就传当前请求的 IP
                template.header(HEADER_X_FORWARDED_FOR, request.getRemoteAddr());
            }
        }
    }
}
