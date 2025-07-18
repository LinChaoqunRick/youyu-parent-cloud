package com.youyu.handler;

import com.alibaba.fastjson.JSON;
import com.youyu.enums.ResultCode;
import com.youyu.result.ResponseResult;
import com.youyu.utils.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 认证异常，你是谁
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        ResponseResult<?> result = ResponseResult.error(ResultCode.UNAUTHORIZED.getCode(), "未认证用户(认证失败)");
        WebUtils.renderString(response, JSON.toJSONString(result));
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}

