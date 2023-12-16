package com.youyu.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.entity.user.PositionInfo;
import com.youyu.entity.user.User;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "user-service")
public interface UserServiceClient {
    @PostMapping(value = "/user/open/selectById")
    ResponseResult<User> selectById(@RequestParam Long userId);

    @PostMapping(value = "/user/follow/selectCountByUserIdTo")
    ResponseResult<Integer> selectCountByUserIdTo(@RequestParam Long userIdTo);

    @PostMapping(value = "/user/follow/getFollowUserIdList")
    ResponseResult<List<Long>> getFollowUserIdList(@RequestParam Long userId);

    @PostMapping(value = "/user/follow/isCurrentUserFollow")
    ResponseResult<Boolean> isCurrentUserFollow(@RequestParam Long userId);

    @RequestMapping("/user/open/pageUserByUserIds")
    ResponseResult<Page<User>> pageUserByUserIds(@RequestParam long current, @RequestParam long size, @RequestParam List<Long> userIds);

    @RequestMapping("/user/open/ipLocation")
    ResponseResult<PositionInfo> ipLocation();
}
