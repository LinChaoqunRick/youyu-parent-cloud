package com.youyu.service.mail.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youyu.constant.RedisConstants;
import com.youyu.dto.ConnectRegisterInput;
import com.youyu.dto.RegisterInput;
import com.youyu.entity.LoginUser;
import com.youyu.entity.auth.UserFramework;
import com.youyu.entity.auth.UserRole;
import com.youyu.enums.ResultCode;
import com.youyu.enums.RoleEnum;
import com.youyu.enums.SMSTemplate;
import com.youyu.exception.SystemException;
import com.youyu.mapper.UserFrameworkMapper;
import com.youyu.mapper.UserRoleMapper;
import com.youyu.service.LoginService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.RedisCache;
import com.youyu.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    @Resource
    private RedisCache redisCache;

    @Resource
    private UserFrameworkMapper userFrameworkMapper;

    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    private UserRoleMapper userRoleMapper;

    private final StringRedisTemplate redisTemplate;

    @Override
    public void logout() {
        // 先获取令牌信息
        Map<String, Object> tokenAttributes = SecurityUtils.getTokenAttributes();
        if (tokenAttributes != null) {
            String jti = String.valueOf(tokenAttributes.get("jti"));
            Long exp = Convert.toLong(tokenAttributes.get("exp"));

            if (exp != null) {
                long currentTimeInSeconds = System.currentTimeMillis() / 1000;
                if (exp > currentTimeInSeconds) {
                    // token未过期，添加至缓存作为黑名单，缓存时间为token剩余的有效时间
                    long remainingTimeInSeconds = exp - currentTimeInSeconds;
                    redisTemplate.opsForValue().set(RedisConstants.Auth.BLACKLIST_TOKEN + jti, "", remainingTimeInSeconds, TimeUnit.SECONDS);
                }
            } else {
                // token 永不过期则永久加入黑名单
                redisTemplate.opsForValue().set(RedisConstants.Auth.BLACKLIST_TOKEN + jti, "");
            }
        }
    }

    @Override
    @Transactional
    public int register(RegisterInput input) {
        UserFramework newUser = new UserFramework();
        LambdaQueryWrapper<UserFramework> queryWrapper = new LambdaQueryWrapper<>();

        if (input.getType() == 0) { // 手机号
            queryWrapper.eq(UserFramework::getUsername, input.getUsername());
            if (userFrameworkMapper.selectCount(queryWrapper) > 0) { // 存在相应邮箱
                throw new SystemException(ResultCode.TELEPHONE_CONFLICT);
            }
            //从redis中获取用户信息
            String redisKey = SMSTemplate.REGISTER_TEMP.getLabel() + ":" + input.getUsername();
            String redisCode = redisCache.getCacheObject(redisKey);
            if (Objects.isNull(redisCode) || !redisCode.equals(input.getCode())) {
                throw new SystemException("700", "验证码错误或已过期");
            }
            newUser.setUsername(input.getUsername());
        } else { // 邮箱
            queryWrapper.eq(UserFramework::getEmail, input.getEmail());
            if (userFrameworkMapper.selectCount(queryWrapper) > 0) { // 存在相应邮箱
                throw new SystemException(ResultCode.EMAIL_CONFLICT);
            }
            //从redis中获取用户信息
            String redisKey = "emailCode:" + input.getEmail();
            String redisCode = redisCache.getCacheObject(redisKey);
            if (Objects.isNull(redisCode) || !redisCode.equals(input.getCode())) {
                throw new SystemException("700", "验证码错误或已过期");
            }
            newUser.setEmail(input.getEmail());
        }

        // 昵称校验
        LambdaQueryWrapper<UserFramework> nicknameQueryWrapper = new LambdaQueryWrapper<>();
        nicknameQueryWrapper.eq(UserFramework::getNickname, input.getNickname());

        Long count = userFrameworkMapper.selectCount(nicknameQueryWrapper);
        if (count > 0) {
            throw new SystemException("700", "昵称“" + input.getNickname() + "”已存在");
        }

        newUser.setNickname(input.getNickname());
        newUser.setPassword(passwordEncoder.encode(input.getPassword()));

        int insert = userFrameworkMapper.insert(newUser);

        Long userId = newUser.getId();

        // 为用户添加角色
        UserRole userRole = new UserRole(userId, RoleEnum.GENERAL_USER.getId());
        userRoleMapper.insert(userRole);

        return insert;
    }

    @Override
    @Transactional
    public UserFramework connectRegister(ConnectRegisterInput input) {
        UserFramework newUser = BeanCopyUtils.copyBean(input, UserFramework.class);
        userFrameworkMapper.insert(newUser);

        Long userId = newUser.getId();

        // 为用户添加角色
        UserRole userRole = new UserRole(userId, RoleEnum.GENERAL_USER.getId());
        userRoleMapper.insert(userRole);

        return newUser;
    }
}
