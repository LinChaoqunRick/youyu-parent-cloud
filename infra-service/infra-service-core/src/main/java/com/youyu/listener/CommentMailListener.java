package com.youyu.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youyu.dto.mail.CommentMailSendInput;
import com.youyu.service.mail.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import java.util.Map;

@Component
@Slf4j
public class CommentMailListener {

    @Resource
    private MailService mailService;

    @Resource
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "commentMail", messageConverter = "jacksonConverter")
    public void CommentMailSend(CommentMailSendInput input) throws Exception {
        // 使用 ObjectMapper 将 input 转换为 Map
        Map<String, String> templateParams = objectMapper.convertValue(input, new TypeReference<>() {});
        // 覆盖需要自定义处理的字段
        // templateParams.put("type", "帖子");
        // 发送邮件
        mailService.sendCommentMail(input.getTo(), templateParams);
    }
}
