package com.youyu.dto.oss;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * OSS 批量签名 URL 请求参数
 *
 * @author youyu
 */
@Data
public class OssBatchSignedUrlInput implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * OSS 对象路径列表
     */
    private List<String> paths;

    /**
     * 过期时间（秒），默认 10 分钟
     */
    private Long expireSeconds = 600L;

    /**
     * 图片处理样式（可选，如：style/thumbnail）
     */
    private String process;

    /**
     * Bucket 名称（可选，不传则使用默认 bucket）
     * 如：album - 使用相册 bucket
     */
    private String bucket;
}