package com.youyu.service.sms.impl;

import com.aliyuncs.exceptions.ClientException;
import com.youyu.config.SendCode;
import com.youyu.dto.sms.SmsSendInput;
import com.youyu.dto.sms.SmsVerifyInput;
import com.youyu.enums.ResultCode;
import com.youyu.enums.SMSTemplate;
import com.youyu.exception.SystemException;
import com.youyu.feign.UserServiceClient;
import com.youyu.service.sms.SmsService;
import com.youyu.utils.NumberUtils;
import com.youyu.utils.RedisCache;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements SmsService {

    @Resource
    private SendCode sendCode;

    @Resource
    private RedisCache redisCache;

    @Resource
    private UserServiceClient userServiceClient;

    @Override
    public boolean send(SmsSendInput input) {
        if (input.isRepeat()) {
            int count = userServiceClient.selectCountByUsername(input.getTelephone()).getData();
            if (count > 0) {
                throw new SystemException(ResultCode.TELEPHONE_CONFLICT);
            }
        }

        // 生成六位随机数
        String code = NumberUtils.createRandomNumber(6);
        // 短信模板id
        String templateCode = SMSTemplate.getCodeById(input.getType());

        boolean send = false;
        try {
            send = sendCode.sendSms(input.getTelephone(), code, templateCode);
        } catch (ClientException e) {
            throw new SystemException("800", e.getMessage());
        }
        // 设置5分钟后过期
        redisCache.setCacheObject(SMSTemplate.getLabelById(input.getType()) + ":" + input.getTelephone(), code, 5, TimeUnit.MINUTES);
        return send;
    }

    @Override
    public boolean verify(SmsVerifyInput input) {
        //从redis中获取用户信息
        String redisKey = SMSTemplate.getLabelById(input.getType()) + ":" + input.getTelephone();
        String redisCode = redisCache.getCacheObject(redisKey);
        if (Objects.isNull(redisCode)) {
            return false;
        }
        return redisCode.equals(input.getCode());
    }

    @Override
    public boolean remove(String key) {
        return redisCache.deleteObject(key);
    }
}
