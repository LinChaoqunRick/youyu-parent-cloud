package com.youyu.controller.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.youyu.annotation.Log;
import com.youyu.config.AlbumOssProperties;
import com.youyu.config.OssProperties;
import com.youyu.dto.album.AlbumDTO;
import com.youyu.dto.oss.OssBatchSignedUrlInput;
import com.youyu.dto.oss.OssSignedUrlInput;
import com.youyu.enums.LogType;
import com.youyu.factory.OssClientFactory;
import com.youyu.feign.AlbumServiceClient;
import com.youyu.result.ResponseResult;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@RefreshScope
@Data
@RestController
@RequestMapping("/oss")
public class OssController {

    @Resource
    private OssProperties ossProperties;

    @Resource
    private AlbumOssProperties albumOssProperties;

    @Resource
    private OssClientFactory ossClientFactory;

    @Resource
    private AlbumServiceClient albumServiceClient;

    /**
     * 服务端签名后直传
     *
     * @return 签名信息
     */
    @RequestMapping("/source/policy")
    @Log(title = "文件上传(policy)", type = LogType.UPLOAD)
    public ResponseResult<Map<String, String>> policy(@RequestParam(defaultValue = "post/images") String base) {
        // date = new Date();
        // int first = date.getYear();

        String format = new SimpleDateFormat("yyyy/MMdd").format(new Date());
        String dir = base + "/" + format + "/"; // 用户上传文件时指定的前缀。

        Map<String, String> respMap = new LinkedHashMap<>();
        // 创建OSSClient实例。
        OSS ossClient = ossClientFactory.getClient();
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            respMap.put("OSSAccessKeyId", ossProperties.getAccessKeyId());
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            /*respMap.put("key", dir);*/ // 文件路径+文件名，前端确定
            respMap.put("host", ossProperties.getHost());
            respMap.put("expire", String.valueOf(expireEndTime / 1000));

        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
        return ResponseResult.success(respMap);
    }

    /**
     * 服务端签名后直传
     *
     * @return 签名信息
     */
    @RequestMapping("/album/policy")
    @Log(title = "获取相册OSS Policy", type = LogType.UPLOAD)
    public ResponseResult<Map<String, String>> policy(@RequestParam Long albumId) {
        AlbumDTO album = albumServiceClient.getById(albumId).getData();

        String dir = "album/"+ album.getUserId() + "_" + albumId + "/"; // 用户上传文件时指定的前缀。

        Map<String, String> respMap = new LinkedHashMap<>();
        // 创建OSSClient实例 - 使用相册专用配置
        OSS ossClient = new OSSClientBuilder().build(
                albumOssProperties.getEndpoint(),
                albumOssProperties.getAccessKeyId(),
                albumOssProperties.getAccessKeySecret()
        );
        try {
            long expireTime = albumOssProperties.getPolicyExpire() != null ? albumOssProperties.getPolicyExpire() : 300;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);

            // 文件大小限制
            long maxSize = albumOssProperties.getPolicyMaxSize() != null ?
                    albumOssProperties.getPolicyMaxSize() * 1024L * 1024L : 11L * 1024L * 1024L;

            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, maxSize);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            respMap.put("OSSAccessKeyId", albumOssProperties.getAccessKeyId());
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", albumOssProperties.getHost());
            respMap.put("expire", String.valueOf(expireEndTime / 1000));

        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
        return ResponseResult.success(respMap);
    }

    /**
     * 生成单个 OSS 对象的签名 URL
     *
     * @param input 签名请求参数
     * @return 签名 URL
     */
    @RequestMapping("/open/generateSignedUrl")
    public ResponseResult<String> generateSignedUrl(@RequestBody OssSignedUrlInput input) {
        // 根据 bucket 参数选择配置
        boolean isAlbumBucket = "album".equalsIgnoreCase(input.getBucket());
        String bucketName = isAlbumBucket ? albumOssProperties.getBucket() : ossProperties.getBucket();
        String endpoint = isAlbumBucket ? albumOssProperties.getEndpoint() : ossProperties.getEndPoint();
        String accessKeyId = isAlbumBucket ? albumOssProperties.getAccessKeyId() : ossProperties.getAccessKeyId();
        String accessKeySecret = isAlbumBucket ? albumOssProperties.getAccessKeySecret() : ossProperties.getAccessKeySecret();

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            // 指定签名URL过期时间
            Date expiration = new Date(System.currentTimeMillis() + input.getExpireSeconds() * 1000);
            GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(
                    bucketName,
                    input.getPath(),
                    com.aliyun.oss.HttpMethod.GET
            );
            req.setExpiration(expiration);

            // 如果有图片处理样式，则添加
            if (StringUtils.hasText(input.getProcess())) {
                req.setProcess(input.getProcess());
            }

            URL signedUrl = ossClient.generatePresignedUrl(req);
            return ResponseResult.success(signedUrl.toString());
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 批量生成 OSS 对象的签名 URL
     *
     * @param input 批量签名请求参数
     * @return path -> signedUrl 的映射
     */
    @RequestMapping("/open/generateBatchSignedUrl")
    public ResponseResult<Map<String, String>> generateBatchSignedUrl(@RequestBody OssBatchSignedUrlInput input) {
        // 根据 bucket 参数选择配置
        boolean isAlbumBucket = "album".equalsIgnoreCase(input.getBucket());
        String bucketName = isAlbumBucket ? albumOssProperties.getBucket() : ossProperties.getBucket();
        String endpoint = isAlbumBucket ? albumOssProperties.getEndpoint() : ossProperties.getEndPoint();
        String accessKeyId = isAlbumBucket ? albumOssProperties.getAccessKeyId() : ossProperties.getAccessKeyId();
        String accessKeySecret = isAlbumBucket ? albumOssProperties.getAccessKeySecret() : ossProperties.getAccessKeySecret();

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            Map<String, String> resultMap = new HashMap<>();
            Date expiration = new Date(System.currentTimeMillis() + input.getExpireSeconds() * 1000);

            for (String path : input.getPaths()) {
                GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(
                        bucketName,
                        path,
                        com.aliyun.oss.HttpMethod.GET
                );
                req.setExpiration(expiration);

                // 如果有图片处理样式，则添加
                if (StringUtils.hasText(input.getProcess())) {
                    req.setProcess(input.getProcess());
                }

                URL signedUrl = ossClient.generatePresignedUrl(req);
                resultMap.put(path, signedUrl.toString());
            }

            return ResponseResult.success(resultMap);
        } finally {
            ossClient.shutdown();
        }
    }
}
