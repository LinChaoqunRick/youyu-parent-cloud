package com.youyu.config;

import com.youyu.dto.backup.DataBaseBackupInput;
import com.youyu.dto.backup.DataBaseBackupOutput;
import com.youyu.service.backup.DataBaseBackupService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronExpression;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * MySQL自动备份定时任务配置
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "mysql.backup", name = "enable-auto-backup", havingValue = "true", matchIfMissing = true)
public class DataBaseBackupConfig implements SchedulingConfigurer {

    @Resource
    private DataBaseBackupProperties backupProperties;

    @Resource
    private DataBaseBackupService dataBaseBackupService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(
                // 要执行的任务
                this::executeAutoBackup,
                // 触发器配置
                triggerContext -> {
                    String cronExpressionStr = backupProperties.getCronExpression();
                    log.debug("MySQL自动备份定时任务cron表达式: {}", cronExpressionStr);

                    // 解析cron表达式
                    CronExpression cronExpression = CronExpression.parse(cronExpressionStr);

                    // 获取下次执行时间
                    Instant lastCompletionTime = triggerContext.lastCompletion();
                    LocalDateTime lastCompletionDateTime = lastCompletionTime != null
                            ? LocalDateTime.ofInstant(lastCompletionTime, ZoneId.systemDefault())
                            : LocalDateTime.now();
                    LocalDateTime nextExecutionTime = cronExpression.next(lastCompletionDateTime);
                    return nextExecutionTime != null
                            ? nextExecutionTime.atZone(ZoneId.systemDefault()).toInstant()
                            : null;
                }
        );

        log.info("MySQL自动备份定时任务已启动，cron表达式: {}", backupProperties.getCronExpression());
    }

    /**
     * 执行自动备份
     */
    private void executeAutoBackup() {
        try {
            log.info("========== 开始执行MySQL自动备份 ==========");

            DataBaseBackupInput input = new DataBaseBackupInput();
            input.setUploadToOss(true);
            input.setRemark("自动备份");

            DataBaseBackupOutput result = dataBaseBackupService.backup(input);

            log.info("MySQL自动备份完成");
            log.info("  - 备份文件: {}", result.getFileName());
            log.info("  - 文件大小: {}", result.getFileSizeReadable());
            log.info("  - 本地路径: {}", result.getLocalPath());
            log.info("  - OSS路径: {}", result.getOssPath());
            log.info("  - 备份耗时: {} ms", result.getDuration());
            log.info("==========================================");

        } catch (Exception e) {
            log.error("MySQL自动备份失败", e);
        }
    }
}