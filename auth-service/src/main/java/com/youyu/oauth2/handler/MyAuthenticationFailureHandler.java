package com.youyu.oauth2.handler;

import com.youyu.entity.Logs;
import com.youyu.enums.LogType;
import com.youyu.enums.ResultCode;
import com.youyu.result.ResponseResult;
import com.youyu.service.LogsService;
import com.youyu.utils.LocateUtils;
import com.youyu.utils.RequestUtils;
import com.youyu.utils.SecurityUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

/**
 * 认证失败处理器
 *
 * @author haoxr
 * @since 3.0.0
 */
@Slf4j
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

    /**
     * MappingJackson2HttpMessageConverter 是 Spring 框架提供的一个 HTTP 消息转换器，用于将 HTTP 请求和响应的 JSON 数据与 Java 对象之间进行转换
     */
    private final HttpMessageConverter<Object> accessTokenHttpResponseConverter = new MappingJackson2HttpMessageConverter();
    private final LogsService logsService;
    private final LocateUtils locateUtils;

    public MyAuthenticationFailureHandler(LogsService logsService, LocateUtils locateUtils) {
        this.logsService = logsService;
        this.locateUtils = locateUtils;
    }


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        OAuth2Error error = ((OAuth2AuthenticationException) exception).getError();
        long startTime = System.currentTimeMillis();
        // 登录失败日志（不影响响应流程）
        try {
            Logs log = new Logs();
            log.setClientId(SecurityUtils.getClientId());
            log.setIp(RequestUtils.getClientIp());
            try {
                log.setAdcode(locateUtils.queryTencentIp().getAdcode());
            } catch (Exception ignored) {}
            log.setPath("/oauth2/token");
            log.setName("登录");
            log.setType(LogType.LOGIN.getCode());
            log.setMethod(request != null ? request.getMethod() : "");
            log.setDuration(System.currentTimeMillis() - startTime);
            log.setResult(0);
            log.setError(error != null ? error.getErrorCode() : exception.getMessage());
            logsService.saveLog(log);
        } catch (Exception ignored) {}

        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        ResponseResult<?> result = ResponseResult.error(ResultCode.FORBIDDEN.getCode(), error.getErrorCode());
        accessTokenHttpResponseConverter.write(result, null, httpResponse);
    }
}
