package com.youyu.service.mail.impl;

import com.youyu.entity.auth.UserFramework;
import com.youyu.entity.auth.AuthParamsEntity;
import com.youyu.enums.ResultCode;
import com.youyu.enums.SMSTemplate;
import com.youyu.exception.SystemException;
import com.youyu.mapper.UserFrameworkMapper;
import com.youyu.service.AuthService;
import com.youyu.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service("sms_authService")
public class SmsAuthServiceImpl implements AuthService {
    @Resource
    private UserFrameworkMapper userFrameworkMapper;

    @Resource
    private RedisCache redisCache;

    @Override
    public UserFramework execute(AuthParamsEntity authParamsEntity) {
        String username = authParamsEntity.getUsername();
        String smsCode = authParamsEntity.getSmsCode();
        UserFramework user = userFrameworkMapper.getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("手机号或验证码错误");
        }
        checkSmsCode(username, smsCode);
        return user;
    }

    private void checkSmsCode(String telephone, String code) {
        //从redis中获取用户信息
        String redisKey = SMSTemplate.LOGIN_TEMP.getLabel() + ":" + telephone;
        String redisCode = redisCache.getCacheObject(redisKey);
        if (redisCode == null) {
            redisCode = "";
        }
        if (!redisCode.equals(code)) {
            throw new SystemException(ResultCode.CODE_INCORRECT);
        }
    }
}
