package com.youyu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 相册 OSS 配置
 *
 * @author youyu
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.album-oss")
public class AlbumOssProperties {
    /**
     * OSS 存储空间
     */
    private String bucket;

    /**
     * 访问身份验证中用到用户标识
     */
    private String accessKeyId;

    /**
     * 用户用于加密签名字符串的密钥
     */
    private String accessKeySecret;

    /**
     * OSS 对外服务的访问域名
     */
    private String endpoint;

    /**
     * OSS 访问地址
     */
    private String host;

    /**
     * 签名有效期(秒)
     */
    private Integer policyExpire;

    /**
     * 上传文件大小限制(MB)
     */
    private Integer policyMaxSize;
}