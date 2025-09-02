package com.youyu.filter;

import com.alibaba.fastjson.JSON;
import com.youyu.entity.Logs;
import com.youyu.enums.LogType;
import com.youyu.service.LogsService;
import com.youyu.utils.LocateUtils;
import com.youyu.utils.RequestUtils;
import com.youyu.utils.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

// @Component
public class OAuth2TokenFilter extends OncePerRequestFilter {

    private final LocateUtils locateUtils;
    private final LogsService logsService;

    // 通过构造器注入依赖
    public OAuth2TokenFilter(LocateUtils locateUtils, LogsService logsService) {
        this.locateUtils = locateUtils;
        this.logsService = logsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 只拦截 /oauth2/token
        return !"/oauth2/token".equals(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Logs actionLog = new Logs();
        long startTime = System.currentTimeMillis();

        try {
            actionLog.setUserId(SecurityUtils.getUserId());
            actionLog.setIp(RequestUtils.getClientIp());
            actionLog.setAdcode(locateUtils.queryTencentIp().getAdcode());
            actionLog.setPath(request.getRequestURI());
            actionLog.setName("登录");
            actionLog.setType(LogType.LOGOUT.getCode());
            actionLog.setMethod(request.getMethod());

            // 请求参数
            Map<String, String[]> params = request.getParameterMap();
            try {
                actionLog.setRequestData(JSON.toJSONString(params));
            } catch (Exception e) {
                actionLog.setRequestData("[参数序列化失败]");
            }

            actionLog.setResult(1);
        } catch (Exception ex) {
            actionLog.setResult(0);
            actionLog.setError(ex.getMessage());
            throw ex;
        } finally {
            actionLog.setDuration(System.currentTimeMillis() - startTime);
            logsService.saveLog(actionLog);
        }

        filterChain.doFilter(request, response);
    }
}
