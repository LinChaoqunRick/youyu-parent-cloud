package com.youyu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssProperties {
    private String bucket;
    private String accessKeyId;
    private String accessKeySecret;
    private String endPoint;
    private String host;
    private String roleArn;
    private String accessKeyIdRAM;
    private String accessKeySecretRAM;
    private String endPointRAM;
}
