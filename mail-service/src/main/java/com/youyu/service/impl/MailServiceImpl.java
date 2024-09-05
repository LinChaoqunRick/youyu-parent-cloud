package com.youyu.service.impl;

import com.youyu.dto.moment.MomentCommentListOutput;
import com.youyu.dto.post.comment.CommentListOutput;
import com.youyu.dto.post.post.PostDetailOutput;
import com.youyu.dto.post.post.PostUserOutput;
import com.youyu.entity.moment.Moment;
import com.youyu.entity.moment.MomentUserOutput;
import com.youyu.entity.user.User;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.feign.MomentServiceClient;
import com.youyu.feign.PostServiceClient;
import com.youyu.feign.UserServiceClient;
import com.youyu.service.MailService;
import com.youyu.utils.MailUtils;
import com.youyu.utils.NumberUtils;
import com.youyu.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Resource
    private UserServiceClient userServiceClient;

    @Resource
    private MomentServiceClient momentServiceClient;

    @Resource
    private PostServiceClient postServiceClient;

    @Resource
    private MailUtils mailUtils;

    @Resource
    private TemplateEngine templateEngine;

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

        Context context = new Context();
        context.setVariable("content", code);
        String emailContent = templateEngine.process("MailRegisterCodeTemplate", context);
        try {
            mailUtils.sendHtmlMail(target, subject, emailContent);
            // 设置5分钟后过期
            redisCache.setCacheObject("emailCode:" + target, code, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean sendPostCommentMailNotice(CommentListOutput input) {
        PostUserOutput user = input.getUser();
        PostUserOutput userTo = input.getUserTo();
        PostDetailOutput post = postServiceClient.selectById(input.getPostId()).getData();

        Context context = new Context();
        context.setVariable("nickname", userTo.getNickname());
        context.setVariable("caption", "用户@" + user.getNickname() + " 在你的博客《" + post.getTitle() + "》下留言了：");
        context.setVariable("content", input.getContent());
        context.setVariable("url", "https://v2.youyul.com/post/details/" + post.getId());
        String emailContent = templateEngine.process("MailReplyTemplate", context);
        try {
            mailUtils.sendHtmlMail(userTo.getEmail(), "[有语] 您有一条新的留言", emailContent);
            log.info("文章评论通知邮件已发送至:{}", userTo.getEmail());
        } catch (Exception e) {
            log.error("文章评论通知邮件发送失败：{}", e.getMessage());
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
        return true;
    }

    @Override
    public Boolean sendMomentCommentMailNotice(MomentCommentListOutput detail) {
        // 获取双方用户信息
        MomentUserOutput user = detail.getUser();
        MomentUserOutput userTo = detail.getUserTo();
        Moment moment = momentServiceClient.getMomentById(detail.getMomentId()).getData();

        // 回复人已绑定邮箱
        Context context = new Context();
        context.setVariable("nickname", userTo.getNickname());
        context.setVariable("caption", "用户@" + user.getNickname() + " 在你的时刻《" + moment.getContent() + "》下留言了：");
        context.setVariable("content", detail.getContent());
        context.setVariable("url", "https://v2.youyul.com/moment/details/" + moment.getId());
        String emailContent = templateEngine.process("MailReplyTemplate", context);
        try {
            mailUtils.sendHtmlMail(userTo.getEmail(), "[有语] 您有一条新的留言", emailContent);
            log.info("时刻评论通知邮件已发送至: {}", userTo.getEmail());
        } catch (Exception e) {
            log.error("时刻评论通知邮件发送失败: {}", e.getMessage());
            return false;
        }
        return true;
    }
}
