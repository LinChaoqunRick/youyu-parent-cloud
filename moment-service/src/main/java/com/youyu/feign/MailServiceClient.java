package com.youyu.feign;

import com.youyu.dto.moment.MomentCommentListOutput;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "mail-service")
public interface MailServiceClient {

    @PostMapping(value = "/mail/sendMomentCommentMailNotice")
    ResponseResult<Boolean> sendMomentCommentMailNotice(MomentCommentListOutput detail);
}
