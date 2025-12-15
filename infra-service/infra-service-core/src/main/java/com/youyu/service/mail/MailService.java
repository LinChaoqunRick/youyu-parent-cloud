package com.youyu.service.mail;

import com.youyu.dto.backup.DataBaseBackupOutput;

import java.util.Map;

public interface MailService {
    Boolean sendRegisterCode(String target, boolean repeat);
    void sendCommentMail(String to, Map<String, String> templateParams) throws Exception;

    /**
     * 发送数据库备份通知邮件
     *
     * @param to 收件人邮箱
     * @param backupResult 备份结果
     * @throws Exception 发送异常
     */
    void sendDatabaseBackupMail(String to, DataBaseBackupOutput backupResult) throws Exception;
}
