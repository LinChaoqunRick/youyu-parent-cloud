package com.youyu.feign;

import com.youyu.entity.moment.Moment;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "moment-service")
public interface MomentServiceClient {
    @PostMapping(value = "/user/open/selectCount")
    ResponseResult<Moment> getById(@RequestParam Long momentId);
}