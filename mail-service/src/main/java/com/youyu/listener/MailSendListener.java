package com.youyu.listener;

import com.youyu.dto.mail.MailReplyInput;
import com.youyu.dto.moment.MomentCommentListOutput;
import com.youyu.service.MailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;

@Component
public class MailSendListener {

    @Resource
    private MailService mailService;

    @RabbitListener(queues = "postCommentMail", messageConverter = "jacksonConverter")
    public void postCommentListener(MailReplyInput input) throws MessagingException {
        mailService.sendPostCommentMailNotice(input);
    }

    @RabbitListener(queues = "momentCommentMail", messageConverter = "jacksonConverter")
    public void momentCommentListener(MomentCommentListOutput input) throws MessagingException {
        mailService.sendMomentCommentMailNotice(input);
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
