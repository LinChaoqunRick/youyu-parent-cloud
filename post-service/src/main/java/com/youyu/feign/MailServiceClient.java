package com.youyu.feign;

import com.youyu.dto.mail.MailReplyInput;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "mail-service")
public interface MailServiceClient {

    @PostMapping(value = "/mail/sendPostCommentMailNotice")
    ResponseResult<Boolean> sendPostCommentMailNotice(MailReplyInput input);
}
