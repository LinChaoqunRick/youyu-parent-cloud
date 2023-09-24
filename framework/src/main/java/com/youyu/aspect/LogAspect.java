package com.youyu.aspect;

import com.youyu.entity.ActionLog;
import com.youyu.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
            StringBuilder builder = createLogStyle(actionLog);
            log.info(String.valueOf(builder));
        }
        return result;
    }

    private ActionLog aroundLogBefore(ProceedingJoinPoint point) {
        ActionLog actionLog = new ActionLog();
        // 获取userId
        actionLog.setUserId(SecurityUtils.getUserId());
        // 获取request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        // 获取ip
        actionLog.setIp(request.getRemoteHost());
        // 获取URI
        actionLog.setPath(request.getRequestURI());
        // 获取API信息
        Method m = ((MethodSignature) point.getSignature()).getMethod();
        ApiOperation apiOperation = m.getAnnotation(ApiOperation.class);

        if (Objects.nonNull(apiOperation)) {
            actionLog.setAction(apiOperation.value());
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
