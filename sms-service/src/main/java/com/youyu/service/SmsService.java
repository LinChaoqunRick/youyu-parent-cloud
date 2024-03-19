package com.youyu.service;

import com.youyu.dto.SmsSendInput;
import com.youyu.dto.SmsVerifyInput;

public interface SmsService {
    boolean send(SmsSendInput input);

    boolean verify(SmsVerifyInput input);

    boolean remove(String key);
}
