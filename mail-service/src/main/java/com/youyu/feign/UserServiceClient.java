package com.youyu.feign;

import com.youyu.entity.user.User;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service")
public interface UserServiceClient {
    @PostMapping(value = "/user/open/selectCountByEmail")
    ResponseResult<Integer> selectCountByEmail(@RequestParam String email);

    @PostMapping(value = "/user/open/selectById")
    ResponseResult<User> selectById(@RequestParam Long userId);
}
