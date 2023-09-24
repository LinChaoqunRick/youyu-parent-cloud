package com.youyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youyu.dto.login.ResultUser;
import com.youyu.entity.LoginUser;
import com.youyu.entity.Route;
import com.youyu.entity.UserFramework;
import com.youyu.entity.UserRole;
import com.youyu.enums.ResultCode;
import com.youyu.enums.RoleEnum;
import com.youyu.enums.SMSTemplate;
import com.youyu.exception.SystemException;
import com.youyu.mapper.LoginMapper;
import com.youyu.mapper.UserFrameworkMapper;
import com.youyu.mapper.UserRoleMapper;
import com.youyu.authentication.sms.SmsCodeAuthenticationToken;
import com.youyu.service.LoginService;
import com.youyu.utils.DateUtils;
import com.youyu.utils.JwtUtil;
import com.youyu.utils.RedisCache;
import io.jsonwebtoken.Claims;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private UserFrameworkMapper userFrameworkMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Resource
    private HttpServletRequest request;

    @Resource
    private HttpServletResponse response;

    @Override
    public ResultUser login(UserFramework user) {
        // AuthenticationManager authenticate;
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 如果认证没通过，给出相应提示
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("登录失败");
        }
        // 如果认证通过了，使用userId生成一个jwt jwt才存入ResponseResult返回

        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        UserFramework result = loginUser.getUser();
        String userId = result.getId().toString();
        String jwt = JwtUtil.createJWT(userId);

        ResultUser resultUser = new ResultUser(result, jwt);
        // 把完整的用户信息存入redis userId作为key
        redisCache.setCacheObject("user:" + userId, loginUser);
        return resultUser;
    }

    @Override
    public ResultUser loginTelephone(String telephone, String code) {
        SmsCodeAuthenticationToken authenticationToken = new SmsCodeAuthenticationToken(telephone, code);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        // 如果认证没通过，给出相应提示
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("登录失败");
        }
        // 如果认证通过了，使用userId生成一个jwt jwt才存入ResponseResult返回

        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        UserFramework result = loginUser.getUser();
        String userId = result.getId().toString();
        String jwt = JwtUtil.createJWT(userId);

        ResultUser resultUser = new ResultUser(result, jwt);
        // 把完整的用户信息存入redis userId作为key
        redisCache.setCacheObject("user:" + userId, loginUser);
        return resultUser;
    }

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
    public List<Route> getAuthRoutes(Long id) {
        return loginMapper.getAuthRoutes(id);
    }

    @Override
    public List<Route> getRoutesByRoleId(Long roleId) {
        return loginMapper.getRoutesByRoleId(roleId);
    }

    @Override
    public int register(String nickname, String username, String email, String password, String code, int type) {
        UserFramework newUser = new UserFramework();
        LambdaQueryWrapper<UserFramework> queryWrapper = new LambdaQueryWrapper<>();

        if (type == 0) { // 手机号
            queryWrapper.eq(UserFramework::getUsername, username);
            if (userFrameworkMapper.selectCount(queryWrapper) > 0) { // 存在相应邮箱
                throw new SystemException(ResultCode.TELEPHONE_CONFLICT);
            }
            //从redis中获取用户信息
            String redisKey = SMSTemplate.REGISTER_TEMP.getLabel() + ":" + username;
            String redisCode = redisCache.getCacheObject(redisKey);
            if (Objects.isNull(redisCode) || !redisCode.equals(code)) {
                throw new SystemException(700, "验证码错误或已过期");
            }
            newUser.setUsername(username);
        } else { // 邮箱
            queryWrapper.eq(UserFramework::getEmail, email);
            if (userFrameworkMapper.selectCount(queryWrapper) > 0) { // 存在相应邮箱
                throw new SystemException(ResultCode.EMAIL_CONFLICT);
            }
            //从redis中获取用户信息
            String redisKey = "emailCode:" + email;
            String redisCode = redisCache.getCacheObject(redisKey);
            if (Objects.isNull(redisCode) || !redisCode.equals(code)) {
                throw new SystemException(700, "验证码错误或已过期");
            }
            newUser.setEmail(email);
        }

        // 昵称校验
        LambdaQueryWrapper<UserFramework> nicknameQueryWrapper = new LambdaQueryWrapper<>();
        nicknameQueryWrapper.eq(UserFramework::getNickname, nickname);

        Integer count = userFrameworkMapper.selectCount(nicknameQueryWrapper);
        if (count > 0) {
            throw new SystemException(700, "昵称“" + nickname + "”已存在");
        }

        newUser.setNickname(nickname);
        newUser.setPassword(passwordEncoder.encode(password));

        int insert = userFrameworkMapper.insert(newUser);

        Long userId = newUser.getId();

        // 为用户添加角色
        UserRole userRole = new UserRole(userId, RoleEnum.GENERAL_USER.getId());
        userRoleMapper.insert(userRole);

        return insert;
    }

    @Override
    public UserFramework getUserById(Long id) {
        UserFramework user = userFrameworkMapper.getUserById(id);
        return user;
    }

    @Override
    public String refreshToken() {
        String token = request.getHeader("token");
        if (token == null) {
            response.setStatus(HttpStatus.SC_PAYMENT_REQUIRED);
            throw new RuntimeException("必填参数token不能为空");
        }
        Claims claims = null;
        try {
            claims = JwtUtil.parseJWTException(token);  // 解析token
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (claims == null) {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException("token解析异常，请重新登录");
        } else {
            // 如果过期时间超过一定时间，则强制要求用户重新登录，否则刷新token
            long expireTime = claims.getExpiration().getTime();
            long nowTime = (new Date()).getTime();
            long diffDay = DateUtils.getAbsTimeStampDiffDay(expireTime, nowTime);
            if (diffDay > 15) {
                response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                throw new RuntimeException("token失效时间过长，请重新登录");
            } else {
                String userId = claims.getSubject();
                // 从redis中获取用户信息，如果信息还存在，创建并返回新的token。如果不存在，报错，前端情况登录信息
                String redisKey = "user:" + userId;
                LoginUser loginUser = redisCache.getCacheObject(redisKey);
                if (loginUser == null) {
                    response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                    throw new RuntimeException("用户信息不存在，请重新登录");
                } else {
                    return JwtUtil.createJWT(userId);
                }
            }
        }
    }
}
