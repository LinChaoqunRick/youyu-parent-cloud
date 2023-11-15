package com.youyu.controller;

import com.youyu.dto.SmsSendInput;
import com.youyu.dto.SmsVerifyInput;
import com.youyu.result.ResponseResult;
import com.youyu.service.SmsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private SmsService smsService;

    @RequestMapping(value = "/send")
    public ResponseResult<Boolean> send(@Valid SmsSendInput input) {
        boolean send = smsService.send(input);
        return ResponseResult.success(send);
    }

    @RequestMapping(value = "/verify")
    public ResponseResult<Boolean> verify(@Valid SmsVerifyInput input) {
        boolean verify = smsService.verify(input);
        return ResponseResult.success(verify);
    }
}
