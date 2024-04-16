package com.youyu.filter;

import com.alibaba.fastjson.JSON;
import com.youyu.entity.LoginUser;
import com.youyu.enums.ResultCode;
import com.youyu.result.ResponseResult;
import com.youyu.utils.RedisCache;
import com.youyu.utils.SecurityUtils;
import com.youyu.utils.WebUtils;
import org.apache.http.HttpStatus;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 如果请求中不存在userId，则放行，让后面的拦截器拦截它
        String authenticateUserId = request.getHeader("AuthenticateUserId"); //获取认证userId
        if (!StringUtils.hasText(authenticateUserId)) {
            filterChain.doFilter(request, response);
            return;
        }

        //从redis中获取用户信息
        String redisKey = "user:" + authenticateUserId;
        LoginUser loginUser = redisCache.getCacheObject(redisKey);
        if (Objects.isNull(loginUser)) {
            ResponseResult result = ResponseResult.error(ResultCode.UNAUTHORIZED.getCode(), "用户信息获取失败");
            WebUtils.renderString(response, JSON.toJSONString(result));
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            return;
        }

        //获取权限信息封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        authenticationToken.setDetails(new OAuth2AuthenticationDetails(request));

        //存入SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //放行
        filterChain.doFilter(request, response);
    }
}
