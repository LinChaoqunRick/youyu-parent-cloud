package com.youyu.controller.mail;

import com.youyu.annotation.Log;
import com.youyu.dto.moment.MomentCommentListOutput;
import com.youyu.dto.post.comment.CommentListOutput;
import com.youyu.enums.LogType;
import com.youyu.result.ResponseResult;
import com.youyu.service.mail.MailService;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/mail")
public class MailController {
    @Resource
    private MailService mailService;

    @RequestMapping("/open/sendRegisterCode")
    @Log(title = "发送注册邮件验证码", type = LogType.NOTIFY_MAIL)
    public ResponseResult<Boolean> sendRegisterCode(@RequestParam String target,
                                                    @RequestParam(required = false, defaultValue = "false") boolean repeat) {
        Boolean result = mailService.sendRegisterCode(target, repeat);
        return ResponseResult.success(result);
    }
}
