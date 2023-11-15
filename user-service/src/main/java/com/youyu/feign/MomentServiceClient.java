package com.youyu.feign;

import com.youyu.dto.moment.MomentListOutput;
import com.youyu.dto.post.post.PostListOutput;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "moment-service")
public interface MomentServiceClient {
    @PostMapping(value = "/moment/open/momentListByIds")
    ResponseResult<List<MomentListOutput>> momentListByIds(@RequestParam List<Long> momentIds);
}
