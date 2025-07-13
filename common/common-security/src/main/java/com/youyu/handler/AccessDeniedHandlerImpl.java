package com.youyu.handler;

import com.alibaba.fastjson.JSON;
import com.youyu.result.ResponseResult;
import com.youyu.utils.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 授权异常，你能做什么，必须在SecurityConfig中通过antMatchers("/post/setPostLike").hasAuthority("test")才能拦截到
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        accessDeniedException.printStackTrace();
        ResponseResult result = ResponseResult.error(HttpStatus.FORBIDDEN.value(), "权限不足权限不足权限不足");
        // 响应给前端
        WebUtils.renderString(response, JSON.toJSONString(result));

    }
}

