package com.youyu.config;

import com.alibaba.fastjson.JSON;
import com.youyu.entity.LoginUser;
import com.youyu.entity.UserFramework;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Map<String, Object> additionalInfo = new HashMap<>();
        UserFramework user = loginUser.getUser();
        user.setPassword(null);
        additionalInfo.put("userInfo", user);
        // additionalInfo.put("userId", loginUser.getUser().getId());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
