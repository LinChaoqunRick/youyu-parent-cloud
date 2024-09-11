package com.youyu.aspect;

import com.alibaba.fastjson.JSON;
import com.youyu.entity.auth.ActionLog;
import com.youyu.utils.RequestUtils;
import com.youyu.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

@Component
@Aspect
@Slf4j
public class LogAspect {

    @Pointcut("execution(* * ..*Controller.create*(..)) " +
            "|| execution(* * ..*Controller.update*(..)) " +
            "|| execution(* * ..*Controller.delete*(..))")
    public void ctrlPointCut() {
    }

    @Around("ctrlPointCut()")
    public Object aroundLog(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        long endTime;
        ActionLog actionLog = new ActionLog();
        Object result;

        try {
            actionLog = aroundLogBefore(point);
            result = point.proceed();
            aroundLogAfter(point, result);
        } finally {
            endTime = System.currentTimeMillis();
            actionLog.setDuration(endTime - startTime);
//            StringBuilder builder = createLogStyle(actionLog);
//            log.info(String.valueOf(builder));
        }
        return result;
    }

    private ActionLog aroundLogBefore(ProceedingJoinPoint point) {
        HttpServletRequest request = RequestUtils.getRequest(); // 获取request
        Method m = ((MethodSignature) point.getSignature()).getMethod(); // 获取API信息
        ApiOperation apiOperation = m.getAnnotation(ApiOperation.class); // 获取注解信息

        ActionLog actionLog = new ActionLog();
        actionLog.setUserId(SecurityUtils.getUserId()); // 获取userId
        actionLog.setIp(RequestUtils.getClientIp()); // 获取调用者ip
        if (request != null) {
            actionLog.setPath(request.getRequestURI()); // 获取调用的URI
            actionLog.setParams(JSON.toJSONString(request.getParameterMap())); // 获取请求参数
        }
        if (Objects.nonNull(apiOperation)) {
            actionLog.setAction(apiOperation.value());
            actionLog.setType(apiOperation.tags()[0]);
        }
        return actionLog;
    }

    private void aroundLogAfter(ProceedingJoinPoint point, Object result) {
        // todo...日志入库
    }

    public StringBuilder createLogStyle(ActionLog actionLog) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("userId:[").append(actionLog.getUserId());
        buffer.append("] ip:[").append(actionLog.getIp());
        buffer.append("] path:[").append(actionLog.getPath());
        buffer.append("] actionName:[").append(actionLog.getAction());
        buffer.append("] duration:[").append(actionLog.getDuration());
        buffer.append("ms]");
        return buffer;
    }
}
