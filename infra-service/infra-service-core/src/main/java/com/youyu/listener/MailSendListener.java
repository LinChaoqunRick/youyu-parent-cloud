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
public class MailSendListener {

    @Resource
    private MailService mailService;

    @RabbitListener(queues = "postCommentMail", messageConverter = "jacksonConverter")
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

    /*@RabbitListener(queues = "momentCommentMail", messageConverter = "jacksonConverter")
    public void momentCommentListener(MomentCommentListOutput input) throws Exception {
        Actor actor = input.getActor();
        Actor actorTo = input.getActorTo();
        MomentListOutput moment = contentServiceClient.getMomentById(input.getMomentId()).getData();
        String actorEmail = userServiceClient.getActorEmailById(actor.getId(), actor.getType()).getData();
        String actorToEmail = userServiceClient.getActorEmailById(actorTo.getId(), actorTo.getType()).getData();
        if (actorEmail.equals(actorToEmail)) {
            // 回复自己，不发送邮件
            return;
        }
        Map<String, String> templateParams = new HashMap<>();
        templateParams.put("actor_to_nickname", actorTo.getNickname());
        templateParams.put("actor_nickname", actor.getNickname());
        templateParams.put("type", "时刻");
        templateParams.put("title", StringUtils.ellipsisUnicode(moment.getContent(), 64));
        templateParams.put("action", input.getRootId() > 0 ? "回复" : "评论");
        templateParams.put("content", input.getContent());
        templateParams.put("link", "https://v2.youyul.com/moment/details/" + moment.getId());

        mailService.sendCommentMail(actorToEmail, templateParams);
    }*/
}
