package com.youyu.feign;

import com.youyu.dto.moment.MomentListOutput;
import com.youyu.dto.post.post.PostDetailOutput;
import com.youyu.entity.moment.MomentComment;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "content-service")
public interface ContentServiceClient {
    @PostMapping(value = "/post/open/get")
    ResponseResult<PostDetailOutput> getPostById(@RequestParam Long postId);

    @PostMapping(value = "/moment/open/get")
    ResponseResult<MomentListOutput> getMomentById(@RequestParam Long momentId);

    @PostMapping(value = "/momentComment/getById")
    ResponseResult<MomentComment> getMomentCommentById(@RequestParam Long momentId);
}
