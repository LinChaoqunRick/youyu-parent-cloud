package com.youyu.entity.connect;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "connect.github")
public class GithubConstants {
    private String clientId;

    private String clientSecret;

    private String redirectURI;

    private String authorizeURL;

    private String accessTokenURL;

    private String userInfoURL;
}
