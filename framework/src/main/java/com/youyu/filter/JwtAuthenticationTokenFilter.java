package com.youyu.filter;

import com.alibaba.fastjson.JSON;
import com.youyu.entity.LoginUser;
import com.youyu.enums.ResultCode;
import com.youyu.result.ResponseResult;
import com.youyu.utils.JwtUtil;
import com.youyu.utils.RedisCache;
import com.youyu.utils.WebUtils;
import io.jsonwebtoken.Claims;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token) || request.getRequestURI().equals("/login/refreshToken")) {
            // 如果请求中不存在token，则放行，让后面的拦截器拦截它
            filterChain.doFilter(request, response);
            return;
        }
        //解析token
        Claims claims = null;
        try {
            claims = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            // 如果存在token，但是非法的token（token格式不正确、已过期、假的token）
            e.printStackTrace();
            ResponseResult result = ResponseResult.error(ResultCode.NEED_LOGIN);
            WebUtils.renderString(response, JSON.toJSONString(result));
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            return;
        }
        String userId = claims.getSubject();
        //从redis中获取用户信息
        String redisKey = "user:" + userId;
        LoginUser loginUser = redisCache.getCacheObject(redisKey);
        if (Objects.isNull(loginUser)) {
            // token过期 假的token token不存在
            ResponseResult result = ResponseResult.error(ResultCode.NEED_LOGIN);
            WebUtils.renderString(response, JSON.toJSONString(result));
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            return;
        }
        //存入SecurityContextHolder
        //TODO 获取权限信息封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //放行
        filterChain.doFilter(request, response);
    }
}
