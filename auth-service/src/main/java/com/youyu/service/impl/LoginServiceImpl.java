package com.youyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youyu.dto.RegisterInput;
import com.youyu.entity.LoginUser;
import com.youyu.entity.auth.UserFramework;
import com.youyu.entity.auth.UserRole;
import com.youyu.enums.ResultCode;
import com.youyu.enums.RoleEnum;
import com.youyu.enums.SMSTemplate;
import com.youyu.exception.SystemException;
import com.youyu.mapper.LoginMapper;
import com.youyu.mapper.UserFrameworkMapper;
import com.youyu.mapper.UserRoleMapper;
import com.youyu.service.LoginService;
import com.youyu.utils.RedisCache;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private RedisCache redisCache;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private LoginMapper loginMapper;

    @Resource
    private UserFrameworkMapper userFrameworkMapper;

    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private HttpServletRequest request;

    @Resource
    private HttpServletResponse response;

    @Override
    public void logout() {
        // 获取SecurityContextHolder中的用户Id
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userId = loginUser.getUser().getId();
        // 删除redis中的值
        redisCache.deleteObject("user:" + userId);
    }

    @Override
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
                throw new SystemException(700, "验证码错误或已过期");
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
                throw new SystemException(700, "验证码错误或已过期");
            }
            newUser.setEmail(input.getEmail());
        }

        // 昵称校验
        LambdaQueryWrapper<UserFramework> nicknameQueryWrapper = new LambdaQueryWrapper<>();
        nicknameQueryWrapper.eq(UserFramework::getNickname, input.getNickname());

        Integer count = userFrameworkMapper.selectCount(nicknameQueryWrapper);
        if (count > 0) {
            throw new SystemException(700, "昵称“" + input.getNickname() + "”已存在");
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
}
