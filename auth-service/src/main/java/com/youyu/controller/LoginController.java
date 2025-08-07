package com.youyu.controller;

import com.youyu.annotation.Log;
import com.youyu.dto.*;
import com.youyu.entity.auth.UserFramework;
import com.youyu.entity.connect.GithubConstants;
import com.youyu.entity.connect.QQConstants;
import com.youyu.enums.LogType;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.mapper.UserFrameworkMapper;
import com.youyu.result.ResponseResult;
import com.youyu.service.LoginService;
import com.youyu.service.mail.impl.GithubAuthServiceImpl;
import com.youyu.service.mail.impl.QQAuthServiceImpl;
import com.youyu.utils.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/oauth2")
public class LoginController {

    @Resource
    private LoginService loginService;

    @Resource
    private QQConstants qqConstants;

    @Resource
    private GithubConstants githubConstants;

    @Resource
    private GithubAuthServiceImpl githubAuthService;

    @Resource
    private QQAuthServiceImpl qqAuthService;

    @Resource
    private UserFrameworkMapper userFrameworkMapper;

    @RequestMapping("/logout")
    @Log(title = "退出登陆", type = LogType.LOGOUT)
    public ResponseResult<String> logout() {
        loginService.logout();
        return ResponseResult.success("注销成功");
    }

    @Log(title = "注册", type = LogType.REGISTER)
    @RequestMapping("/register")
    @Transactional
    public ResponseResult<Boolean> register(@Valid RegisterInput input) {
        if (input.getType() == 0) {
            if (Objects.isNull(input.getUsername())) {
                throw new SystemException("800", "手机号不能为空");
            }
        } else {
            if (Objects.isNull(input.getEmail())) {
                throw new SystemException("800", "邮箱不能为空");
            }
        }
        int result = loginService.register(input);
        return ResponseResult.success(result == 1);
    }

    @GetMapping("/connect/getUrl")
    public ResponseResult<String> getConnectURL(@Valid ConnectUrlInput input) {
        String url = "";
        String authorizeURL = "";
        String redirectUri = "";
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        if (input.getType().equals("qq")) {
            authorizeURL = qqConstants.getAuthorizeURL();
            redirectUri = qqConstants.getRedirectURI();
            url = authorizeURL +
                    "?response_type=code" +
                    "&client_id=" + qqConstants.getAppID() +
                    "&redirect_uri=" + URLEncoder.encode(redirectUri) +
                    "&state=" + input.getState();
        } else if (input.getType().equals("github")) {
            authorizeURL = githubConstants.getAuthorizeURL();
            redirectUri = githubConstants.getRedirectURI();
            url = authorizeURL +
                    "?response_type=code" +
                    "&client_id=" + githubConstants.getClientId() +
                    "&redirect_uri=" + redirectUri +
                    "&state=" + input.getState();
        }
        return ResponseResult.success(url);
    }

    @RequestMapping("/connect/bind")
    public ResponseResult<Integer> connectBind(@Valid ConnectBindInput input) {
        Integer result = null;
        String type = input.getType();
        Long userId = SecurityUtils.getUserId();

        if (type.equals("github")) {
            GithubUserInfoResult githubUserInfo = githubAuthService.getGithubUserByCode(input.getCode());
            String githubUserId = githubUserInfo.getId();
            UserFramework githubUser = githubAuthService.getUserByGithubId(githubUserId);
            if (githubUser != null) {
                throw new SystemException(ResultCode.OTHER_ERROR.getCode(), "此用户已绑定其它账号");
            } else {
                UserFramework userFramework = new UserFramework();
                userFramework.setId(userId);
                userFramework.setGithubId(githubUserId);
                result = userFrameworkMapper.updateById(userFramework);
            }
        } else if (type.equals("qq")) {
            QQUserInfoResult qqUserInfo = qqAuthService.getQQUserByCode(input.getCode());
            String openId = qqUserInfo.getOpenId();
            UserFramework qqUser = qqAuthService.getUserByQQId(openId);
            if (qqUser != null) {
                throw new SystemException(ResultCode.OTHER_ERROR.getCode(), "此用户已绑定其它账号");
            } else {
                UserFramework userFramework = new UserFramework();
                userFramework.setId(userId);
                userFramework.setQqId(openId);
                result = userFrameworkMapper.updateById(userFramework);
            }
        }
        return ResponseResult.success(result);
    }

    @RequestMapping("/testAccess")
    public ResponseResult<String> testAccess() {
        return ResponseResult.success("testAccess");
    }
}
