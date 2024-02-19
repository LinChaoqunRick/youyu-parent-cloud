package com.youyu.feign;

import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service")
public interface UserServiceClient {
    @PostMapping(value = "/user/open/selectCountByUsername")
    ResponseResult<Integer> selectCountByUsername(@RequestParam String username);
}
