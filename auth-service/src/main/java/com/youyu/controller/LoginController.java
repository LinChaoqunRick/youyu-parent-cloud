package com.youyu.controller;

import com.youyu.exception.SystemException;
import com.youyu.result.ResponseResult;
import com.youyu.service.LoginService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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

    @RequestMapping("/testAccess")
    public ResponseResult<String> testAccess() {
        return ResponseResult.success("testAccess");
    }
}
