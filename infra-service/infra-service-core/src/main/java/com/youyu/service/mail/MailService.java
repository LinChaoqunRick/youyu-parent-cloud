package com.youyu.service.mail;

import com.youyu.dto.backup.DatabaseBackupOutput;

import java.util.Map;

public interface MailService {
    Boolean sendRegisterCode(String target, boolean repeat);
    void sendCommentMail(String to, Map<String, String> templateParams) throws Exception;

    /**
     * 发送数据库备份成功通知邮件
     *
     * @param to 收件人邮箱
     * @param backupResult 备份结果
     * @throws Exception 发送异常
     */
    void sendDatabaseBackupMail(String to, DatabaseBackupOutput backupResult) throws Exception;

    /**
     * 发送数据库备份失败通知邮件
     *
     * @param to 收件人邮箱
     * @param errorMessage 错误信息
     * @param failTime 失败时间
     * @throws Exception 发送异常
     */
    void sendDatabaseBackupFailMail(String to, String errorMessage, String failTime) throws Exception;
}
