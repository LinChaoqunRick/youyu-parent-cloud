package com.youyu.service.impl;

import com.youyu.dto.ConnectRegisterInput;
import com.youyu.dto.GithubAccessTokenResult;
import com.youyu.dto.GithubUserInfoResult;
import com.youyu.entity.auth.AuthParamsEntity;
import com.youyu.entity.auth.UserFramework;
import com.youyu.entity.connect.GithubConstants;
import com.youyu.mapper.UserFrameworkMapper;
import com.youyu.service.AuthService;
import com.youyu.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Slf4j
@Service("github_authService")
public class GithubAuthServiceImpl implements AuthService {

    @Resource
    private UserFrameworkMapper userFrameworkMapper;

    @Resource
    private LoginService loginService;

    @Resource
    private GithubConstants githubConstants;

    @Resource
    private RestTemplate restTemplate;

    @Override
    public UserFramework execute(AuthParamsEntity authParamsEntity) {
        String accessTokenURL = githubConstants.getAccessTokenURL();
        String userInfoURL = githubConstants.getUserInfoURL();
        String clientId = githubConstants.getClientId();
        String clientSecret = githubConstants.getClientSecret();

        // 获取AccessToken
        String fullAccessTokenURL = accessTokenURL + "?client_id=" + clientId + "&client_secret=" + clientSecret + "&code=" + authParamsEntity.getGithubCode();
        String accessTokenResponse = restTemplate.getForObject(fullAccessTokenURL, String.class);

        GithubAccessTokenResult accessTokenResult;
        GithubUserInfoResult userInfoResult;
        UserFramework githubUser;

        if (accessTokenResponse != null) {
            accessTokenResult = parseAccessTokenResponse(accessTokenResponse);
        } else {
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessTokenResult.getAccessToken());
        userInfoResult = restTemplate.exchange(userInfoURL, HttpMethod.GET, new HttpEntity<>(headers), GithubUserInfoResult.class).getBody();

        if (userInfoResult != null) {
            githubUser = getUserByGithubId(userInfoResult.getId());
        } else {
            return null;
        }

        if (githubUser == null) {
            // 用户不存在，注册
            ConnectRegisterInput input = new ConnectRegisterInput();
            input.setNickname(userInfoResult.getLogin());
            input.setAvatar(userInfoResult.getAvatar_url());
            input.setGithubId(userInfoResult.getId());
            githubUser = loginService.connectRegister(input);
        }

        return githubUser;
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

    public UserFramework getUserByGithubId(String githubId) {
        return userFrameworkMapper.getUserByGithubId(githubId);
    }
}