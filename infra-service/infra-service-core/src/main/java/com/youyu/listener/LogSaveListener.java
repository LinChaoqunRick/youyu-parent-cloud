package com.youyu.listener;

import com.youyu.entity.Logs;
import com.youyu.service.LogsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 系统日志保存监听器
 */
@Component
@Slf4j
public class LogSaveListener {

    @Resource
    private LogsService logsService;

    @RabbitListener(queues = "systemLog", messageConverter = "jacksonConverter")
    public void systemLogSave(Logs logMessage) {
        try {
            logsService.save(logMessage);
            // log.debug("日志保存成功: {}", logMessage.getName());
        } catch (Exception e) {
            // log.error("日志保存失败", e);
            throw e; // 抛出异常以便进入死信队列
        }
    }
}