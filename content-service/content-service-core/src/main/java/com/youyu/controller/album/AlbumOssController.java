package com.youyu.controller.album;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.youyu.annotation.Log;
import com.youyu.config.OssProperties;
import com.youyu.entity.album.Album;
import com.youyu.enums.LogType;
import com.youyu.result.ResponseResult;
import com.youyu.service.album.AlbumService;
import lombok.Data;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


@RefreshScope
@Data
@RestController
@RequestMapping("/album/oss")
public class AlbumOssController {

    @Resource
    private OssProperties ossProperties;

    @Resource
    private AlbumService albumService;

    /**
     * 服务端签名后直传
     *
     * @return
     */
    @RequestMapping("/policy")
    @Log(title = "获取相册OSS Policy", type = LogType.UPLOAD)
    public ResponseResult<Map<String, String>> policy(@RequestParam Long albumId) {
        Album album = albumService.getById(albumId);

        String dir = "album/"+ album.getUserId() + "_" + albumId + "/"; // 用户上传文件时指定的前缀。

        Map<String, String> respMap = new LinkedHashMap<>();
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(ossProperties.getEndPoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret());
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

            respMap.put("OSSAccessKeyId", ossProperties.getAccessKeyId());
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
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
}
