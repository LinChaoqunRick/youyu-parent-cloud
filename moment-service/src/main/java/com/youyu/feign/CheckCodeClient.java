package com.youyu.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "mail-service")
public interface CheckCodeClient {

    @PostMapping(value = "/mail/sendMomentReplyNotice")
    public Boolean sendMomentReplyNotice(MailReplyInput input);
}
