package com.youyu.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class AppStartupListener {

    private static final Logger logger = LoggerFactory.getLogger(AppStartupListener.class);

    // 成功启动监听
    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        printStartupInfo(event.getApplicationContext(), true);
    }

    // 启动失败监听
    @EventListener
    public void onApplicationFailed(ApplicationFailedEvent event) {
        printStartupInfo(event.getApplicationContext(), false);
    }

    private void printStartupInfo(ApplicationContext context, boolean isSuccess) {
        // 获取环境信息
        Environment env = context.getEnvironment();
        String[] activeProfiles = env.getActiveProfiles();
        String envProfile = activeProfiles.length > 0 ? 
            String.join(",", activeProfiles) : "default";

        // 格式化启动时间
        long startupTime = context.getStartupDate();
        String formattedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .format(new Date(startupTime));

        // 构建日志信息
        String status = isSuccess ? "成功" : "失败";
        String logMessage = String.format(
            "\n=== 应用启动信息 ===\n" +
            "启动时间: %s\n" +
            "启动状态: %s\n" +
            "运行环境: %s\n" +
            "====================",
            formattedTime, status, envProfile
        );

        // 输出日志
        if (isSuccess) {
            logger.info(logMessage);
        } else {
            logger.error(logMessage);
        }
    }
}