package com.youyu.feign;

import com.youyu.entity.user.User;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "user-service")
public interface UserServiceClient {
    @PostMapping(value = "/user/open/selectById")
    ResponseResult<User> selectById(@RequestParam Long userId);

    @PostMapping(value = "/user/open/listByIds")
    ResponseResult<List<User>> listByIds(@RequestParam("userIds") List<Long> userIds);

    @PostMapping(value = "/user/follow/getUserFollowCount")
    ResponseResult<Integer> getUserFollowCount(@RequestParam Long userId);

    @PostMapping(value = "/user/follow/isCurrentUserFollow")
    ResponseResult<Boolean> isCurrentUserFollow(@RequestParam Long userId);
}
