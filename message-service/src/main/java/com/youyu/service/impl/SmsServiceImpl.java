package com.youyu.service.impl;

import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youyu.config.SendCode;
import com.youyu.dto.SmsSendInput;
import com.youyu.dto.SmsVerifyInput;
import com.youyu.entity.User;
import com.youyu.enums.ResultCode;
import com.youyu.enums.SMSTemplate;
import com.youyu.exception.SystemException;
import com.youyu.service.SmsService;
import com.youyu.service.UserService;
import com.youyu.utils.NumberUtils;
import com.youyu.utils.RedisCache;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements SmsService {

    @Resource
    private SendCode sendCode;

    @Resource
    private RedisCache redisCache;

    @Resource
    private UserService userService;

    @Override
    public boolean send(SmsSendInput input) {
        if (input.isRepeat()) {
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getUsername, input.getTelephone());
            int count = userService.count(lambdaQueryWrapper);
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
            throw new SystemException(800, e.getMessage());
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
}
