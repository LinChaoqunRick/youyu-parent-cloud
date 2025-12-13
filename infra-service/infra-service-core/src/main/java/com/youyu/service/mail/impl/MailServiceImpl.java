package com.youyu.service.mail.impl;

import com.youyu.annotation.Log;
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
}
