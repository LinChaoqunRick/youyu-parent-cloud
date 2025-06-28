package com.youyu.aspect;

import com.alibaba.fastjson.JSON;
import com.youyu.annotation.Log;
import com.youyu.entity.BusinessLog;
import com.youyu.service.BusinessLogService;
import com.youyu.utils.LocateUtils;
import com.youyu.utils.RequestUtils;
import com.youyu.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Resource
    private BusinessLogService businessLogService;

    @Resource
    private LocateUtils locateUtils;

    @Pointcut("@annotation(com.youyu.annotation.Log)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);

        long startTime = System.currentTimeMillis();
        BusinessLog actionLog = new BusinessLog();
        Object result;

        try {
            fillLogBefore(joinPoint, logAnnotation, actionLog);

            result = joinPoint.proceed();
            actionLog.setResult(1); // 成功

            // 保存响应结果
            if (logAnnotation.saveResponseData() && result != null) {
                try {
                    actionLog.setResponseData(JSON.toJSONString(result));
                } catch (Exception e) {
                    actionLog.setResponseData("[响应序列化失败]");
                }
            }

            return result;

        } catch (Throwable ex) {
            actionLog.setResult(0); // 失败

            String errorMsg = ex.getMessage();
            if (errorMsg != null && errorMsg.length() > 500) {
                errorMsg = errorMsg.substring(0, 500) + "...";
            }
            actionLog.setError(errorMsg);

            throw ex;
        } finally {
            actionLog.setDuration(System.currentTimeMillis() - startTime);
            businessLogService.saveLog(actionLog);
        }
    }


    private void fillLogBefore(ProceedingJoinPoint joinPoint, Log logAnnotation, BusinessLog businessLog) {
        HttpServletRequest request = RequestUtils.getRequest();

        businessLog.setUserId(SecurityUtils.getUserId());
        businessLog.setIp(RequestUtils.getClientIp());
        businessLog.setAdcode(locateUtils.getUserPositionByIP().getAdcode());
        businessLog.setPath(request != null ? request.getRequestURI() : "");
        businessLog.setName(logAnnotation.title());
        businessLog.setType(logAnnotation.type().getCode());
        businessLog.setMethod(request != null ? request.getMethod() : "");

        // 请求参数处理
        if (logAnnotation.savaRequestData()) {
            Object[] args = joinPoint.getArgs();
            String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
            Set<String> exclude = new HashSet<>(Arrays.asList(logAnnotation.excludeParamNames()));
            Map<String, Object> params = new HashMap<>();

            for (int i = 0; i < paramNames.length; i++) {
                if (!exclude.contains(paramNames[i]) && !(args[i] instanceof HttpServletRequest)) {
                    params.put(paramNames[i], args[i]);
                }
            }

            try {
                businessLog.setRequestData(JSON.toJSONString(params));
            } catch (Exception e) {
                businessLog.setRequestData("[参数序列化失败]");
            }
        }
    }
}
