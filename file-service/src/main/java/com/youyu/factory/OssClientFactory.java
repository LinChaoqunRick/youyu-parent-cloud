package com.youyu.factory;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.youyu.config.OssProperties;
import org.springframework.stereotype.Component;

@Component
public class OssClientFactory {

    private final OssProperties ossProperties;

    public OssClientFactory(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    /**
     * 获取 OSS 客户端
     */
    public OSS getClient() {
        return new OSSClientBuilder().build(
                ossProperties.getEndPoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret()
        );
    }
}
