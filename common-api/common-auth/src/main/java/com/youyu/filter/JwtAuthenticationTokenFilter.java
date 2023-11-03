package com.youyu.filter;

import com.alibaba.fastjson.JSON;
import com.youyu.entity.LoginUser;
import com.youyu.enums.ResultCode;
import com.youyu.result.ResponseResult;
import com.youyu.utils.RedisCache;
import com.youyu.utils.WebUtils;
import org.apache.http.HttpStatus;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
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

    @Resource
    @Lazy
    private TokenStore tokenStore;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            // 如果请求中不存在token，则放行，让后面的拦截器拦截它
            filterChain.doFilter(request, response);
            return;
        }

        //解析token
        OAuth2AccessToken accessToken;
        try {
            accessToken = tokenStore.readAccessToken(token);
            boolean expired = accessToken.isExpired();
            if (expired) {
                throw new RuntimeException("Token已过期");
            }
        } catch (Exception e) {
            // 如果存在token，但是非法的token（token格式不正确、已过期、假的token）
            e.printStackTrace();
            ResponseResult result = ResponseResult.error(ResultCode.NEED_LOGIN.getCode(), e.getMessage());
            WebUtils.renderString(response, JSON.toJSONString(result));
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            return;
        }
        String userId = String.valueOf(accessToken.getAdditionalInformation().get("user_id"));
        //从redis中获取用户信息
        String redisKey = "user:" + userId;
        LoginUser loginUser = redisCache.getCacheObject(redisKey);
        if (Objects.isNull(loginUser)) {
            ResponseResult result = ResponseResult.error(ResultCode.NEED_LOGIN.getCode(), "用户信息获取失败");
            WebUtils.renderString(response, JSON.toJSONString(result));
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            return;
        }

        //获取权限信息封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                request));

        //存入SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //放行
        filterChain.doFilter(request, response);
    }
}
