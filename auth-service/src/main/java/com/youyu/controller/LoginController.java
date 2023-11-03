package com.youyu.controller;

import com.youyu.dto.login.ResultUser;
import com.youyu.entity.UserFramework;
import com.youyu.enums.ResultCode;
import com.youyu.enums.RoleEnum;
import com.youyu.exception.SystemException;
import com.youyu.result.ResponseResult;
import com.youyu.service.LoginService;
import com.youyu.utils.SecurityUtils;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/oauth")
public class LoginController {

    @Resource
    private TokenEndpoint tokenEndpoint;

    @Resource
    private LoginService loginService;

    @RequestMapping("/token")
    public ResponseResult<OAuth2AccessToken> postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        OAuth2AccessToken body = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        return ResponseResult.success(body);
    }

    @RequestMapping("/logout")
    public ResponseResult logout() {
        loginService.logout();
        return ResponseResult.success("注销成功");
    }

    @RequestMapping("/getAuthRoutes")
    public ResponseResult getAuthRoutes(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StringUtils.hasText(token)) {
            return ResponseResult.success(loginService.getAuthRoutes(SecurityUtils.getUserId()));
        } else {
            return ResponseResult.success(loginService.getRoutesByRoleId(RoleEnum.NO_LOGGED_USER.getId()));
        }
    }

    @RequestMapping("/register")
    @Transactional
    public ResponseResult<Boolean> getAuthRoutes(@RequestParam String nickname,
                                                 @RequestParam(required = false) String username,
                                                 @RequestParam(required = false) String email,
                                                 @RequestParam String password,
                                                 @RequestParam String code,
                                                 @RequestParam int type) {
        if (type == 0) {
            if (Objects.isNull(username)) {
                throw new SystemException(800, "手机号不能为空");
            }
        } else {
            if (Objects.isNull(email)) {
                throw new SystemException(800, "邮箱不能为空");
            }
        }
        int result = loginService.register(nickname, username, email, password, code, type);
        return ResponseResult.success(result == 1);
    }

    @RequestMapping("/getCurrentUser")
    public ResponseResult<UserFramework> getCurrentUser() {
        Long currentUserId = SecurityUtils.getUserId();
        UserFramework user;
        if (Objects.nonNull(currentUserId)) {
            user = loginService.getUserById(currentUserId);
        } else {
            throw new SystemException(ResultCode.USER_NOT_EXIST);
        }
        return ResponseResult.success(user);
    }
}
