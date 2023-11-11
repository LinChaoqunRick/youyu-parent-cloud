package com.youyu.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.entity.user.User;
import com.youyu.entity.user.UserFollow;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(value = "user-service")
public interface UserServiceClient {
    @PostMapping(value = "/user/open/selectById")
    ResponseResult<User> selectById(Long userId);

    @PostMapping(value = "/user/follow/selectCount")
    ResponseResult<Integer> selectCount(LambdaQueryWrapper<UserFollow> input);

    @PostMapping(value = "/user/follow/getFollowUserIdList")
    ResponseResult<List<Long>> getFollowUserIdList(Long userId);

    @PostMapping(value = "/user/follow/isCurrentUserFollow")
    ResponseResult<Boolean> isCurrentUserFollow(Long userId);

    @PostMapping(value = "/user/open/selectPage")
    ResponseResult<Page<User>> selectPage(Page<User> page, LambdaQueryWrapper<User> lambdaQueryWrapper);
}
