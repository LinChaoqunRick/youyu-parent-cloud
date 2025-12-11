package com.youyu.listener;

import com.youyu.dto.mail.CommentMailSendInput;
import com.youyu.service.mail.MailService;
import com.youyu.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CommentMailListener {

    @Resource
    private MailService mailService;

    @RabbitListener(queues = "commentMail", messageConverter = "jacksonConverter")
    public void postCommentListener(CommentMailSendInput input) throws Exception {
        Map<String, String> templateParams = new HashMap<>();
        templateParams.put("actor_to_nickname", input.getActorToNickName());
        templateParams.put("actor_nickname", input.getActorNickname());
        templateParams.put("type", "帖子");
        templateParams.put("title", StringUtils.ellipsisUnicode(input.getTitle(), 20));
        templateParams.put("action", input.getCommentType() == 0 ? "评论" : "回复");
        templateParams.put("content", input.getContent());
        templateParams.put("link", input.getLink());
        // 发送邮件
        mailService.sendCommentMail(input.getTo(), templateParams);
    }
}
