package com.youyu.service.mail.impl;

import com.youyu.dto.ConnectRegisterInput;
import com.youyu.dto.QQAccessTokenResult;
import com.youyu.dto.QQUserInfoResult;
import com.youyu.entity.auth.AuthParamsEntity;
import com.youyu.entity.auth.UserFramework;
import com.youyu.entity.connect.QQConstants;
import com.youyu.mapper.UserFrameworkMapper;
import com.youyu.service.AuthService;
import com.youyu.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * qq授权登录
 * 文档地址: https://wiki.connect.qq.com/%e5%bc%80%e5%8f%91%e6%94%bb%e7%95%a5_server-side
 */
@Slf4j
@Service("qq_authService")
public class QQAuthServiceImpl implements AuthService {

    @Resource
    private QQConstants qqConstants;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private UserFrameworkMapper userFrameworkMapper;

    @Resource
    private LoginService loginService;

    @Override
    public UserFramework execute(AuthParamsEntity authParamsEntity) {
        QQUserInfoResult userInfoResult = getQQUserByCode(authParamsEntity.getQqCode());
        UserFramework qqUser = getUserByQQId(userInfoResult.getOpenId());

        if (qqUser == null) {
            // 用户不存在，注册
            ConnectRegisterInput input = new ConnectRegisterInput();
            input.setNickname(userInfoResult.getNickname());
            input.setAvatar(userInfoResult.getFigureurl_qq());
            input.setQqId(userInfoResult.getOpenId());
            input.setSex(transformGender(userInfoResult.getGender_type()));
            qqUser = loginService.connectRegister(input);
        }

        return qqUser;
    }

    public QQUserInfoResult getQQUserByCode(String code) {
        String accessTokenURL = qqConstants.getAccessTokenURL();
        String openIDURL = qqConstants.getOpenIDURL();
        String appID = qqConstants.getAppID();
        String appKey = qqConstants.getAppKey();
        String redirectURI = qqConstants.getRedirectURI();
        String userInfoURL = qqConstants.getUserInfoURL();

        // 通过Authorization Code获取Access Token
        StringBuilder fullAccessTokenURL = new StringBuilder(accessTokenURL)
                .append("?grant_type=").append("authorization_code")
                .append("&client_id=").append(appID)
                .append("&client_secret=").append(appKey)
                .append("&code=").append(code)
                .append("&redirect_uri=").append(redirectURI);
        String accessTokenResponse = restTemplate.getForObject(fullAccessTokenURL.toString(), String.class);

        QQAccessTokenResult accessTokenResult;

        if (accessTokenResponse != null) {
            accessTokenResult = parseAccessTokenResponse(accessTokenResponse);
        } else {
            return null;
        }

        // 使用Access Token来获取用户的OpenID
        String fullOpenIDURL = openIDURL + "?access_token=" + accessTokenResult.getAccessToken();
        String openIdResponse = restTemplate.getForObject(fullOpenIDURL, String.class);
        String openId = getOpenId(openIdResponse);

        // 使用Access Token以及OpenID来访问和修改用户数据
        StringBuilder fullUserInfoURL = new StringBuilder(userInfoURL)
                .append("?access_token=").append(accessTokenResult.getAccessToken())
                .append("&oauth_consumer_key=").append(appID)
                .append("&openid=").append(openId);
        QQUserInfoResult userInfoResult = restTemplate.getForObject(fullUserInfoURL.toString(), QQUserInfoResult.class);
        userInfoResult.setOpenId(openId);
        return userInfoResult;
    }

    public QQAccessTokenResult parseAccessTokenResponse(String response) {
        QQAccessTokenResult tokenResponse = new QQAccessTokenResult();
        String[] pairs = response.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", -1); // -1 parameter makes split keep empty strings
            String value = keyValue.length > 1 ? keyValue[1] : ""; // check length before accessing
            switch (keyValue[0]) {
                case "access_token":
                    tokenResponse.setAccessToken(value);
                    break;
                case "expires_in":
                    tokenResponse.setExpireIn(value);
                    break;
            }
        }
        return tokenResponse;
    }

    public String getOpenId(String resultString) {
        String pattern = "\"openid\":\"(.*?)\"";

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(resultString);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public UserFramework getUserByQQId(String qqId) {
        return userFrameworkMapper.getUserByQQId(qqId);
    }

    public Integer transformGender(Integer qqGender) {
        if (qqGender == 2) {
            return 0;
        } else {
            return qqGender;
        }
    }
}
