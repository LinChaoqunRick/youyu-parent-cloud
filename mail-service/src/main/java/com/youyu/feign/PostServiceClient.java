package com.youyu.feign;

import com.youyu.dto.post.post.PostDetailOutput;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "post-service")
public interface PostServiceClient {
    @PostMapping(value = "/post/open/get")
    ResponseResult<PostDetailOutput> selectById(@RequestParam Long postId);
}
