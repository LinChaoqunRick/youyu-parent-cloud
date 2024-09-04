package com.youyu.listener;

import com.youyu.dto.mail.MailReplyInput;
import com.youyu.dto.moment.MomentCommentListOutput;
import com.youyu.dto.post.comment.CommentListOutput;
import com.youyu.service.MailService;
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
        try {
            mailService.sendPostCommentMailNotice(input);
        } catch (Exception ignored) {

        }
    }

    @RabbitListener(queues = "momentCommentMail", messageConverter = "jacksonConverter")
    public void momentCommentListener(MomentCommentListOutput input) {
        try {
            mailService.sendMomentCommentMailNotice(input);
        } catch (Exception ignored) {

        }
    }

    @RabbitListener(queues = "dl-PostComment", messageConverter = "jacksonConverter")
    public void dlPostCommentQueue(MailReplyInput input) {
        System.out.println(input);
    }

    @RabbitListener(queues = "dl-MomentComment", messageConverter = "jacksonConverter")
    public void dlMomentCommentQueue(MomentCommentListOutput input) {
        System.out.println(input);
    }
}
