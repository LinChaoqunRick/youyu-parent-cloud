package com.youyu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.youyu.dto.ConnectUrlInput;
import com.youyu.dto.GithubAccessTokenResult;
import com.youyu.dto.GithubUserInfoResult;
import com.youyu.dto.RegisterInput;
import com.youyu.entity.connect.GithubConstants;
import com.youyu.entity.connect.QQConstants;
import com.youyu.exception.SystemException;
import com.youyu.result.ResponseResult;
import com.youyu.service.LoginService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.Principal;
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
    private RestTemplate restTemplate;

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
    public ResponseResult<Boolean> getAuthRoutes(@Valid RegisterInput input) {
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
        // String uuid = UUID.randomUUID().toString().replaceAll("-", "");

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
                    "&connect_type=" + input.getType();
        }
        return ResponseResult.success(url);
    }

    @GetMapping("/connect/githubLogin")
    public ResponseResult<GithubUserInfoResult> githubLogin(@RequestParam String code) throws JsonProcessingException {
        String accessTokenURL = githubConstants.getAccessTokenURL();
        String userInfoURL = githubConstants.getUserInfoURL();
        String clientId = githubConstants.getClientId();
        String clientSecret = githubConstants.getClientSecret();

        // 获取AccessToken
        String fullAccessTokenURL = accessTokenURL + "?client_id=" + clientId + "&client_secret=" + clientSecret + "&code=" + code;
        String accessTokenResponse = restTemplate.getForObject(fullAccessTokenURL, String.class);
        GithubAccessTokenResult accessTokenResult = parseAccessTokenResponse(accessTokenResponse);

        // 获取UserInfo
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessTokenResult.getAccessToken());
        GithubUserInfoResult userInfoResult = restTemplate.exchange(userInfoURL, HttpMethod.GET, new HttpEntity<>(headers), GithubUserInfoResult.class).getBody();

        // 调用/oauth/token接口

        return ResponseResult.success(userInfoResult);
    }

    @RequestMapping("/testAccess")
    public ResponseResult<String> testAccess() {
        return ResponseResult.success("testAccess");
    }

    public static GithubAccessTokenResult parseAccessTokenResponse(String response) {
        GithubAccessTokenResult tokenResponse = new GithubAccessTokenResult();
        String[] pairs = response.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", -1); // -1 parameter makes split keep empty strings
            String value = keyValue.length > 1 ? keyValue[1] : ""; // check length before accessing
            switch (keyValue[0]) {
                case "access_token":
                    tokenResponse.setAccessToken(value);
                    break;
                case "scope":
                    tokenResponse.setScope(value);
                    break;
                case "token_type":
                    tokenResponse.setTokenType(value);
                    break;
            }
        }
        return tokenResponse;
    }



    public static void main(String[] args) throws JsonProcessingException {
        String accessTokenResponse = "access_token=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&scope=&token_type=bearer";
        GithubAccessTokenResult accessTokenResult = parseAccessTokenResponse(accessTokenResponse);
        GithubAccessTokenResult accessTokenResult1 = parseAccessTokenResponse(accessTokenResponse);
    }
}
