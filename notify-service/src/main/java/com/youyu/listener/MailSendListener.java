package com.youyu.listener;

import com.youyu.dto.moment.MomentCommentListOutput;
import com.youyu.dto.post.comment.CommentListOutput;
import com.youyu.service.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MailSendListener {

    @Resource
    private MailService mailService;

    @RabbitListener(queues = "postCommentMail", messageConverter = "jacksonConverter")
    public void postCommentListener(CommentListOutput input) {
        mailService.sendPostCommentMailNotice(input);
    }

    @RabbitListener(queues = "momentCommentMail", messageConverter = "jacksonConverter")
    public void momentCommentListener(MomentCommentListOutput input) {
        mailService.sendMomentCommentMailNotice(input);
    }
}
