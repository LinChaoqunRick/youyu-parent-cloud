package com.youyu.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youyu.dto.MailReplyInput;
import com.youyu.entity.auth.UserFramework;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.mapper.UserFrameworkMapper;
import com.youyu.service.MailService;
import com.youyu.utils.MailUtils;
import com.youyu.utils.NumberUtils;
import com.youyu.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.util.concurrent.TimeUnit;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private UserFrameworkMapper userMapper;

    @Autowired
    private MailUtils mailUtils;

    @Resource
    private TemplateEngine templateEngine;

    @Autowired
    private RedisCache redisCache;

    @Override
    public Boolean sendRegisterCode(String target, boolean repeat) throws MessagingException {
        if (repeat) { // 不发送给已存在的邮箱
            LambdaQueryWrapper<UserFramework> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(UserFramework::getEmail, target);
            Integer count = userMapper.selectCount(lambdaQueryWrapper);
            if (count > 0) {
                throw new SystemException(ResultCode.EMAIL_CONFLICT);
            }
        }
        // 主题
        String subject = "邮件验证码";
        // 内容
        String code = NumberUtils.createRandomNumber(6);

        Context context = new Context();
        context.setVariable("content", code);
        String emailContent = templateEngine.process("MailRegisterCodeTemplate", context);
        mailUtils.sendHtmlMail(target, subject, emailContent);
        // 设置5分钟后过期
        redisCache.setCacheObject("emailCode:" + target, code, 5, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public Boolean sendPostReplyNotice(MailReplyInput input) {
        Context context = new Context();
        context.setVariable("nickname", input.getNickname());
        context.setVariable("caption", input.getCaption());
        context.setVariable("content", input.getContent());
        context.setVariable("url", input.getUrl());
        String emailContent = templateEngine.process("MailReplyTemplate", context);
        try {
            mailUtils.sendHtmlMail(input.getTarget(), input.getSubject(), emailContent);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Boolean sendMomentReplyNotice(MailReplyInput input) throws MessagingException {
        Context context = new Context();
        context.setVariable("nickname", input.getNickname());
        context.setVariable("caption", input.getCaption());
        context.setVariable("content", input.getContent());
        context.setVariable("url", input.getUrl());
        String emailContent = templateEngine.process("MailReplyTemplate", context);
        try {
            mailUtils.sendHtmlMail(input.getTarget(), input.getSubject(), emailContent);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return true;
    }
}
