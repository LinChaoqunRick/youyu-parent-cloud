package com.youyu.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youyu.dto.mail.MailReplyInput;
import com.youyu.dto.moment.MomentCommentListOutput;
import com.youyu.entity.moment.Moment;
import com.youyu.entity.user.User;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.feign.MomentServiceClient;
import com.youyu.feign.UserServiceClient;
import com.youyu.service.MailService;
import com.youyu.utils.MailUtils;
import com.youyu.utils.NumberUtils;
import com.youyu.utils.RedisCache;
import com.youyu.utils.SecurityUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class MailServiceImpl implements MailService {

    @Resource
    private UserServiceClient userServiceClient;

    @Resource
    private MomentServiceClient momentServiceClient;

    @Resource
    private MailUtils mailUtils;

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private RedisCache redisCache;

    @Override
    public Boolean sendRegisterCode(String target, boolean repeat) throws MessagingException {
        if (repeat) { // 不发送给已存在的邮箱
            Integer count = userServiceClient.selectCountByEmail(target).getData();
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
    public Boolean sendPostCommentMailNotice(MailReplyInput input) {
        System.out.println(input.getNickname());
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
    public Boolean sendMomentCommentNotice(MailReplyInput input) {
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
            return false;
        }
        return true;
    }

    @Async
    @Override
    public Boolean sendMomentCommentMailNotice(MomentCommentListOutput detail) {
        Long currentUserId = SecurityUtils.getUserId();
        if (currentUserId.equals(detail.getUserIdTo())) {
            return false; // 不发给自己
        }

        // 获取双方用户信息
        User user = userServiceClient.selectById(detail.getUserId()).getData();
        User userTo = userServiceClient.selectById(detail.getUserIdTo()).getData();

        // 获取时刻信息
        Moment moment = momentServiceClient.getById(detail.getMomentId()).getData();

        // 回复人已绑定邮箱
        if (Objects.nonNull(userTo) && StringUtils.hasText(userTo.getEmail())) {
            MailReplyInput mailReplyInput = new MailReplyInput();
            mailReplyInput.setTarget(userTo.getEmail());
            mailReplyInput.setNickname(userTo.getNickname());
            mailReplyInput.setSubject("[有语] 您有一条新的留言");
            mailReplyInput.setCaption("用户@" + user.getNickname() + " 在你的时刻《" + moment.getContent() + "》下留言了：");
            mailReplyInput.setContent(detail.getContent());
            mailReplyInput.setUrl("http://v2.youyul.com/details/details/" + moment.getId());
            return sendMomentCommentNotice(mailReplyInput);

        }
        return false;
    }
}
