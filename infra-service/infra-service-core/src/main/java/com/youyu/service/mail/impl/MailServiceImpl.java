package com.youyu.service.mail.impl;

import com.youyu.annotation.Log;
import com.youyu.dto.backup.DataBaseBackupOutput;
import com.youyu.enums.EmailTemplate;
import com.youyu.enums.LogType;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.feign.UserServiceClient;
import com.youyu.service.mail.AliyunEmailService;
import com.youyu.service.mail.MailService;
import com.youyu.utils.NumberUtils;
import com.youyu.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Resource
    private UserServiceClient userServiceClient;

    @Resource
    private AliyunEmailService aliyunEmailService;

    @Resource
    private RedisCache redisCache;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Boolean sendRegisterCode(String target, boolean repeat) {
        if (repeat) { // 不发送给已存在的邮箱
            Integer count = userServiceClient.selectCountByEmail(target).getData();
            if (count > 0) {
                throw new SystemException(ResultCode.EMAIL_CONFLICT);
            }
        }
        String subject = "邮件验证码";
        String code = NumberUtils.createRandomNumber(6);

//        Context context = new Context();
//        context.setVariable("content", code);
//        String emailContent = templateEngine.process("MailRegisterCodeTemplate", context);
//        try {
//            mailUtils.sendHtmlMail(target, subject, emailContent);
//            // 设置5分钟后过期
//            redisCache.setCacheObject("emailCode:" + target, code, 5, TimeUnit.MINUTES);
//        } catch (Exception e) {
//            return false;
//        }
        return true;
    }

    @Override
    @Log(title = "发送文章评论通知邮件", type = LogType.NOTIFY_MAIL)
    public void sendCommentMail(String to, Map<String, String> templateParams) throws Exception {
        aliyunEmailService.sendTemplateMail(EmailTemplate.COMMENT_MAIL.getCode(), to, templateParams);
    }

    @Override
    @Log(title = "发送数据库备份通知邮件", type = LogType.NOTIFY_MAIL)
    public void sendDatabaseBackupMail(String to, DataBaseBackupOutput backupResult) throws Exception {
        Map<String, String> templateParams = new HashMap<>();
        templateParams.put("subject", "【系统通知】MySQL数据库备份完成");
        templateParams.put("fileName", backupResult.getFileName());
        templateParams.put("fileSize", backupResult.getFileSizeReadable());
        templateParams.put("backupTime", backupResult.getBackupTime().format(DATE_TIME_FORMATTER));
        templateParams.put("duration", String.valueOf(backupResult.getDuration()));
        templateParams.put("localPath", backupResult.getLocalPath() != null ? backupResult.getLocalPath() : "-");
        templateParams.put("ossPath", backupResult.getOssPath() != null ? backupResult.getOssPath() : "-");
        templateParams.put("uploadStatus", backupResult.getUploadedToOss() ? "已上传" : "未上传");
        templateParams.put("remark", backupResult.getRemark() != null ? backupResult.getRemark() : "-");

        aliyunEmailService.sendTemplateMail(EmailTemplate.DATABASE_BACKUP_MAIL.getCode(), to, templateParams);
        log.info("数据库备份通知邮件已发送到: {}", to);
    }
}
