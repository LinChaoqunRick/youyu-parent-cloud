package com.youyu.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Component
public class AppStatusListener {

    private static final Logger logger = LoggerFactory.getLogger(AppStatusListener.class);
    private static final int LOG_WIDTH = 70; // 日志总宽度（按中文字符计算）
    private long startupTime;
    private String appName;
    private Environment env;
    private String hostAddress;
    private String port;

    //========================= 启动事件处理 =========================//
    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        ApplicationContext context = event.getApplicationContext();
        this.env = context.getEnvironment();
        this.startupTime = context.getStartupDate();
        this.appName = env.getProperty("spring.application.name", "UNKNOWN");
        try {
            this.hostAddress = InetAddress.getLocalHost().getHostAddress();
            this.port = env.getProperty("server.port", "8080");
            printStartupInfo(context, true);
        } catch (Exception e) {
            logger.error("启动信息收集失败: {}", e.getMessage());
        }
    }

    @EventListener
    public void onApplicationFailed(ApplicationFailedEvent event) {
        printStartupInfo(event.getApplicationContext(), false);
    }

    //========================= 停止事件处理 =========================//
    @Async
    @EventListener
    public void onContextClosed(ContextClosedEvent event) {
        printShutdownInfo();
    }

    //========================= 核心方法 =========================//
    private void printStartupInfo(ApplicationContext context, boolean isSuccess) {
        try {
            Environment env = context.getEnvironment();
            String[] profiles = env.getActiveProfiles();
            String activeProfiles = profiles.length > 0 ?
                    String.join(",", profiles) : "default";

            // 时间格式化
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            String startupTimeStr = sdf.format(new Date(startupTime));

            // JVM 信息
            String javaVersion = System.getProperty("java.version");
            String jvmMemory = String.format(
                    "初始: %dMB / 最大: %dMB",
                    Runtime.getRuntime().totalMemory() / 1024 / 1024,
                    Runtime.getRuntime().maxMemory() / 1024 / 1024
            );

            // 构建日志
            String status = isSuccess ? "✅ 成功" : "❌ 失败";
            String logContent = buildBox(
                    "应用启动报告",
                    new String[] {
                            formatLine("应用名称", appName),
                            formatLine("启动时间", startupTimeStr),
                            formatLine("启动状态", status),
                            formatLine("运行环境", activeProfiles),
                            formatLine("服务端口", port),
                            formatLine("主机IP", hostAddress),
                            formatLine("Java版本", javaVersion),
                            formatLine("JVM内存", jvmMemory),
                            formatLine("Spring Boot版本",
                                    SpringApplication.class.getPackage().getImplementationVersion())
                    }
            );

            if (isSuccess) {
                logger.info(logContent);
            } else {
                logger.error(logContent);
            }
        } catch (Exception e) {
            logger.error("生成启动日志失败: {}", e.getMessage());
        }
    }

    private void printShutdownInfo() {
        try {
            long shutdownTime = System.currentTimeMillis();
            long uptime = shutdownTime - startupTime;
            String uptimeStr = formatUptime(uptime);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            String shutdownTimeStr = sdf.format(new Date(shutdownTime));

            String logContent = buildBox(
                    "应用停止报告",
                    new String[] {
                            formatLine("应用名称", appName),
                            formatLine("停止时间", shutdownTimeStr),
                            formatLine("累计运行", uptimeStr),
                            formatLine("最后访问地址", "http://" + hostAddress + ":" + port)
                    }
            );
            logger.info(logContent);
        } catch (Exception e) {
            logger.error("生成停止日志失败: {}", e.getMessage());
        }
    }

    //========================= 工具方法 =========================//
    private String buildBox(String title, String[] lines) {
        StringBuilder sb = new StringBuilder("\n");
        String border = "═".repeat(LOG_WIDTH - 4); // 减去边界的空格

        // 顶部边框
        sb.append("╔═").append(title).append("═")
                .append(border.substring(title.length() + 2))
                .append("╗\n");

        // 内容行
        for (String line : lines) {
            sb.append("║ ").append(line).append(" ║\n");
        }

        // 底部边框
        sb.append("╚").append(border).append("╝");
        return sb.toString();
    }

    private String formatLine(String label, String value) {
        String fullText = label + ": " + value;
        int requiredPadding = LOG_WIDTH - 4 - calculateDisplayWidth(fullText);
        if (requiredPadding > 0) {
            return fullText + " ".repeat(requiredPadding);
        }
        return fullText;
    }

    private String formatUptime(long millis) {
        return String.format("%d天 %02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toDays(millis),
                TimeUnit.MILLISECONDS.toHours(millis) % 24,
                TimeUnit.MILLISECONDS.toMinutes(millis) % 60,
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        );
    }

    private int calculateDisplayWidth(String text) {
        return text.chars().map(c -> (c >= 0x4E00 && c <= 0x9FA5) ? 2 : 1).sum();
    }
}