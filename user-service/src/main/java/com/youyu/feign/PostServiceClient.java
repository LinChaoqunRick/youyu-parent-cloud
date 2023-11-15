package com.youyu.feign;

import com.youyu.dto.post.post.PostListOutput;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "post-service")
public interface PostServiceClient {
    @PostMapping(value = "/post/open/postListByIds")
    ResponseResult<List<PostListOutput>> postListByIds(@RequestParam List<Long> postIds);
}
