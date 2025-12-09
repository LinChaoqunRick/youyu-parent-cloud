package com.youyu.config;

import com.aliyun.credentials.Client;
import com.aliyun.credentials.models.Config;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class AliyunMailConfig {

    @Value("${aliyun.access.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.access.accessKeySecret}")
    private String accessKeySecret;
    @Value("${aliyun.mail.endpoint}")
    private String endpoint;

    public com.aliyun.dm20151123.Client getMailClient() throws Exception {
        Config credentialConfig = new Config();
        credentialConfig.setType("access_key");
        // 必填参数，此处以从环境变量中获取AccessKey ID为例
        credentialConfig.setAccessKeyId(accessKeyId);
        // 必填参数，此处以从环境变量中获取AccessKey Secret为例
        credentialConfig.setAccessKeySecret(accessKeySecret);
        Client credentialClient = new Client(credentialConfig);
        // 若使用云产品 V2.0 SDK 时，使用com.aliyun.teaopenapi.models.Config传递credential
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.setCredential(credentialClient);
        config.setEndpoint(endpoint);
        return new com.aliyun.dm20151123.Client(config);
    }

}
