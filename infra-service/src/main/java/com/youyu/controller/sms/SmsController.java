package com.youyu.controller.sms;


import com.youyu.annotation.Log;
import com.youyu.dto.sms.SmsSendInput;
import com.youyu.dto.sms.SmsVerifyInput;
import com.youyu.enums.LogType;
import com.youyu.enums.SMSTemplate;
import com.youyu.result.ResponseResult;
import com.youyu.service.sms.SmsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/sms")
public class SmsController {

    @Resource
    private SmsService smsService;

    @RequestMapping(value = "/open/send")
    @Log(title = "发送短信验证码", type = LogType.NOTIFY_SMS, saveResponseData = true)
    public ResponseResult<Boolean> send(@Valid SmsSendInput input) {
        boolean send = smsService.send(input);
        return ResponseResult.success(send);
    }

    @RequestMapping(value = "/open/verify")
    @Log(title = "验证短信验证码", type = LogType.OTHER)
    public ResponseResult<Boolean> verify(@Valid SmsVerifyInput input) {
        boolean verify = smsService.verify(input);
        if (verify) {
            String redisKey = SMSTemplate.getLabelById(input.getType()) + ":" + input.getTelephone();
            boolean remove = smsService.remove(redisKey);
        }
        return ResponseResult.success(verify);
    }
}
