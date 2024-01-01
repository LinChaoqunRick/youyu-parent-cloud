package com.youyu.controller;

import com.youyu.dto.ConnectUrlInput;
import com.youyu.dto.RegisterInput;
import com.youyu.entity.connect.GithubConstants;
import com.youyu.entity.connect.QQConstants;
import com.youyu.exception.SystemException;
import com.youyu.mapper.UserFrameworkMapper;
import com.youyu.result.ResponseResult;
import com.youyu.service.LoginService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/oauth")
public class LoginController {

    @Resource
    private TokenEndpoint tokenEndpoint;

    @Resource
    private LoginService loginService;

    @Resource
    private QQConstants qqConstants;

    @Resource
    private GithubConstants githubConstants;

    @Resource
    private UserFrameworkMapper userFrameworkMapper;

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
    public ResponseResult<Boolean> register(@Valid RegisterInput input) {
        if (input.getType() == 0) {
            if (Objects.isNull(input.getUsername())) {
                throw new SystemException(800, "手机号不能为空");
            }
        } else {
            if (Objects.isNull(input.getEmail())) {
                throw new SystemException(800, "邮箱不能为空");
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
                    "&redirect_uri=" + redirectUri +
                    "&connect_type=" + input.getType();
        } else if (input.getType().equals("github")) {
            authorizeURL = githubConstants.getAuthorizeURL();
            redirectUri = githubConstants.getRedirectURI();
            url = authorizeURL +
                    "?response_type=code" +
                    "&client_id=" + githubConstants.getClientId() +
                    "&redirect_uri=" + redirectUri +
                    "&connect_type=" + input.getType() +
                    "&stamp=" + uuid;
        }
        return ResponseResult.success(url);
    }

    @PostMapping("/connect/githubLogin")
    public ResponseResult<OAuth2AccessToken> githubLogin(Principal principal, @RequestParam String code) throws HttpRequestMethodNotSupportedException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("client_id", "web");
        parameters.put("client_secret", "654321");
        parameters.put("auth_type", "github");
        parameters.put("github_code", code);
        OAuth2AccessToken body = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        return ResponseResult.success(body);
    }

    @RequestMapping("/testAccess")
    public ResponseResult<String> testAccess() {
        return ResponseResult.success("testAccess");
    }
}
