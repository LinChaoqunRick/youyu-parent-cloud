package com.youyu.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.youyu.entity.Album;
import com.youyu.result.ResponseResult;
import com.youyu.service.AlbumService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


@RefreshScope
@Data
@ConfigurationProperties(prefix = "aliyun.oss")
@RestController
@RequestMapping("/album/oss")
public class AlbumOssController {

    private String bucket;
    // 签名方式
    private String accessKeyId;
    private String accessKeySecret;
    private String endPoint;
    private String host;
    //STS方式
    private String roleArn;
    private String accessKeyIdRAM;
    private String accessKeySecretRAM;
    private String endPointRAM;

    @Resource
    private AlbumService albumService;

    /**
     * 服务端签名后直传
     *
     * @return
     */
    @RequestMapping("/policy")
    public ResponseResult<Map<String, String>> policy(@RequestParam Long albumId) {
        Album album = albumService.getById(albumId);

        String dir = "album/"+ album.getUserId() + "_" + albumId + "/"; // 用户上传文件时指定的前缀。

        Map<String, String> respMap = new LinkedHashMap<>();
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);
        try {
            long expireTime = 180;
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

            respMap.put("OSSAccessKeyId", accessKeyId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));

        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
        return ResponseResult.success(respMap);
    }
}
