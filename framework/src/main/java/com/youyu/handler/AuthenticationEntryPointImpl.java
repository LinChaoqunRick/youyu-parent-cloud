package com.youyu.handler;

import com.alibaba.fastjson.JSON;
import com.youyu.enums.ResultCode;
import com.youyu.result.ResponseResult;
import com.youyu.utils.WebUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 认证异常，你是谁
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        authException.printStackTrace();
        // InsufficientAuthenticationException
        // BadCredentialsException
        ResponseResult result = null;
        if (authException instanceof BadCredentialsException) {
            result = ResponseResult.error(ResultCode.LOGIN_ERROR.getCode(), authException.getMessage() + " BadCredentialsException");
        } else if (authException instanceof InsufficientAuthenticationException) {
            // 无token，但是访问了需要token的接口
//            result = ResponseResult.error(ResultCode.NO_OPERATOR_AUTH.getCode(), authException.getMessage() + " InsufficientAuthenticationException");
            result = ResponseResult.error(ResultCode.NO_OPERATOR_AUTH.getCode(), authException.getMessage() + " 【token不存在】");
        } else {
            result = ResponseResult.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "认证失败认证失败认证失败");
        }
        // 响应给前端
        WebUtils.renderString(response, JSON.toJSONString(result));
    }
}

