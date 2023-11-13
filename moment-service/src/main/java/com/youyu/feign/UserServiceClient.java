package com.youyu.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.entity.user.User;
import com.youyu.entity.user.UserFollow;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "user-service")
public interface UserServiceClient {
    @PostMapping(value = "/user/open/selectById")
    ResponseResult<User> selectById(@RequestParam Long userId);

    @PostMapping(value = "/user/follow/selectCount")
    ResponseResult<Integer> selectCount(LambdaQueryWrapper<UserFollow> input);

    @PostMapping(value = "/user/follow/getFollowUserIdList")
    ResponseResult<List<Long>> getFollowUserIdList(Long userId);

    @PostMapping(value = "/user/follow/isCurrentUserFollow")
    ResponseResult<Boolean> isCurrentUserFollow(Long userId);

    @PostMapping(value = "/user/open/selectPage")
    ResponseResult<Page<User>> selectPage(@RequestParam long current, @RequestParam long size,
                                          @RequestBody(required = false) LambdaQueryWrapper<User> lambdaQueryWrapper);

    @RequestMapping("/user/open/pageUserByUserIds")
    ResponseResult<Page<User>> pageUserByUserIds(@RequestParam long current, @RequestParam long size, @RequestParam List<Long> userIds);
}
