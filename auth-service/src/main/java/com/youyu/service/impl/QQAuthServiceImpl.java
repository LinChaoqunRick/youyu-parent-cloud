package com.youyu.service.impl;

import com.youyu.dto.QQAccessTokenResult;
import com.youyu.entity.auth.AuthParamsEntity;
import com.youyu.entity.auth.UserFramework;
import com.youyu.entity.connect.QQConstants;
import com.youyu.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Slf4j
@Service("qq_authService")
public class QQAuthServiceImpl implements AuthService {

    @Resource
    private QQConstants qqConstants;

    @Resource
    private RestTemplate restTemplate;

    @Override
    public UserFramework execute(AuthParamsEntity authParamsEntity) {
        String accessTokenURL = qqConstants.getAccessTokenURL();
        String openIDURL = qqConstants.getOpenIDURL();
        String appID = qqConstants.getAppID();
        String appKey = qqConstants.getAppKey();
        // 通过Authorization Code获取Access Token
        String fullAccessTokenURL = accessTokenURL + "?client_id=" + appID + "&client_secret=" + appKey + "&code=" + authParamsEntity.getQqCode();
        String accessTokenResponse = restTemplate.getForObject(fullAccessTokenURL, String.class);

        QQAccessTokenResult accessTokenResult;

        if (accessTokenResponse != null) {
            accessTokenResult = parseAccessTokenResponse(accessTokenResponse);
        } else {
            return null;
        }

        // 使用Access Token来获取用户的OpenID
        String fullOpenIDURL = openIDURL + "?access_token=" + accessTokenResult.getAccessToken();


        return null;
    }

    public static QQAccessTokenResult parseAccessTokenResponse(String response) {
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
}
