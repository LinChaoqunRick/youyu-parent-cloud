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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Resource
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        String clientId = "";

        if (authorization.startsWith("Basic ")) {
            // 是 client 认证（登录/token 请求）
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            clientId = auth.getName();
        } else if (authorization.startsWith("Bearer ")) {
            // 是访问资源接口
            String token = authorization.split(" ")[1];
            try {
                Claims claims = JwtUtil.parseJWT(token);
                clientId = (String) claims.get("client_id");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 如果请求中不存在userId，则放行，让后面的拦截器拦截它
        String X_User_ID = request.getHeader("X-User-Id"); //获取认证userId
        if (!StringUtils.hasText(X_User_ID)) {
            filterChain.doFilter(request, response);
            return;
        }

        //从redis中获取用户信息
        String redisKey = clientId + ":" + X_User_ID;
        LoginUser loginUser = redisCache.getCacheObject(redisKey);
        if (Objects.isNull(loginUser)) {
            ResponseResult<?> result = ResponseResult.error(ResultCode.UNAUTHORIZED.getCode(), "用户信息获取失败");
            WebUtils.renderString(response, JSON.toJSONString(result));
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
            return;
        }

        //获取权限信息封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        // 可以设置请求相关信息（非必须）
        // authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        //存入SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //放行
        filterChain.doFilter(request, response);
    }
}
