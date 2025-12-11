package com.youyu.service.mail.impl;

import com.youyu.entity.LoginUser;
import com.youyu.entity.auth.UserFramework;
import com.youyu.entity.auth.AuthParamsEntity;
import com.youyu.enums.ResultCode;
import com.youyu.enums.SMSTemplate;
import com.youyu.exception.SystemException;
import com.youyu.mapper.MenuMapper;
import com.youyu.mapper.UserFrameworkMapper;
import com.youyu.service.AuthService;
import com.youyu.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Slf4j
@Service("sms_authService")
public class SmsAuthServiceImpl implements AuthService {
    @Resource
    private UserFrameworkMapper userFrameworkMapper;

    @Resource
    private RedisCache redisCache;

    @Resource
    private MenuMapper menuMapper;

    @Override
    public LoginUser execute(AuthParamsEntity authParamsEntity, String clientId) {
        String username = authParamsEntity.getUsername();
        String smsCode = authParamsEntity.getSmsCode();
        UserFramework user = userFrameworkMapper.getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("手机号或验证码错误");
        }
        checkSmsCode(username, smsCode);

        List<String> permission = menuMapper.selectPermsByUserId(user.getId());
        return new LoginUser(user, permission);
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
