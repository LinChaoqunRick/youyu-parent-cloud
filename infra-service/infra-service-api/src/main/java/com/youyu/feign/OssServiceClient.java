package com.youyu.feign;

import com.youyu.dto.oss.OssBatchSignedUrlInput;
import com.youyu.dto.oss.OssSignedUrlInput;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = "infra-service")
public interface OssServiceClient {

    /**
     * 生成单个 OSS 对象的签名 URL
     *
     * @param input 签名请求参数
     * @return 签名 URL
     */
    @PostMapping(value = "/oss/open/generateSignedUrl")
    ResponseResult<String> generateSignedUrl(@RequestBody OssSignedUrlInput input);

    /**
     * 批量生成 OSS 对象的签名 URL
     *
     * @param input 批量签名请求参数
     * @return path -> signedUrl 的映射
     */
    @PostMapping(value = "/oss/open/generateBatchSignedUrl")
    ResponseResult<Map<String, String>> generateBatchSignedUrl(@RequestBody OssBatchSignedUrlInput input);
}