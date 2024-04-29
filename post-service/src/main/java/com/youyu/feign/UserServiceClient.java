package com.youyu.feign;

import com.youyu.entity.user.ProfileMenu;
import com.youyu.entity.user.User;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service")
public interface UserServiceClient {
    @PostMapping(value = "/user/open/selectById")
    ResponseResult<User> selectById(@RequestParam Long userId);

    @PostMapping(value = "/user/follow/getFansCount")
    ResponseResult<Integer> getFansCount(@RequestParam Long userId);

    @PostMapping(value = "/user/follow/isFollow")
    ResponseResult<Integer> isFollow(@RequestParam Long userId, @RequestParam Long userIdTo);

    @PostMapping(value = "/user/open/getProfileMenu")
    ResponseResult<ProfileMenu> getProfileMenu(@RequestParam Long userId);
}
