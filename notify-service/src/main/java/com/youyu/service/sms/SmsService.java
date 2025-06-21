package com.youyu.service.sms;

import com.youyu.dto.sms.SmsSendInput;
import com.youyu.dto.sms.SmsVerifyInput;

public interface SmsService {
    boolean send(SmsSendInput input);

    boolean verify(SmsVerifyInput input);

    boolean remove(String key);
}
