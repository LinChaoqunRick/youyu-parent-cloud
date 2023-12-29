package com.youyu.entity.connect;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "connect.qq")
public class QQConstants {

	private String appID;

	private String appKey;

	private String redirectURI;

	private String authorizeURL;

	private String accessTokenURL;

	private String openIDURL;

	private String userInfoURL;

}
