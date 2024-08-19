package com.youyu.listener;

import com.youyu.dto.mail.MailReplyInput;
import com.youyu.service.MailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;

@Component
public class MailSendListener {

    @Resource
    private MailService mailService;

    @RabbitListener(queues = "mail", messageConverter = "jacksonConverter")
    public void test(MailReplyInput input) throws MessagingException, InterruptedException {
        Thread.sleep(1000);
//        System.out.println(input.getTarget());
        mailService.sendPostCommentMailNotice(input);
    }
}
