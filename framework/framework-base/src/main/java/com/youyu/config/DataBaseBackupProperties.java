package com.youyu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "mysql.backup")
public class DataBaseBackupProperties {
    /**
     * MySQL容器名称
     */
    private String containerName;

    /**
     * MySQL用户名
     */
    private String username;

    /**
     * MySQL密码
     */
    private String password;

    /**
     * 本地备份目录
     */
    private String localBackupDir;

    /**
     * OSS备份路径
     */
    private String ossBackupPath;

    /**
     * 本地备份保留天数
     */
    private Integer retentionDays;

    /**
     * 是否启用自动备份
     */
    private Boolean enableAutoBackup;

    /**
     * 自动备份的cron表达式（默认每周日晚上8点）
     */
    private String cronExpression;

    /**
     * 是否启用邮件通知
     */
    private Boolean enableEmailNotify = false;

    /**
     * 备份通知邮件地址（多个邮箱用逗号分隔）
     */
    private String notifyEmail;
}