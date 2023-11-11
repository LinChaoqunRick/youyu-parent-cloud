package com.youyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youyu.entity.user.UserFollow;
import com.youyu.mapper.UserFollowMapper;
import com.youyu.result.ResponseResult;
import com.youyu.service.UserFollowService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user/follow")
public class UserFollowController {
    @Resource
    private UserFollowMapper userFollowMapper;

    @Resource
    private UserFollowService userFollowService;

    @RequestMapping("/selectCount")
    ResponseResult<Integer> followList(LambdaQueryWrapper<UserFollow> input) {
        return ResponseResult.success(userFollowMapper.selectCount(input));
    }

    @RequestMapping("/getFollowUserIdList")
    ResponseResult<List<Long>> getFollowUserIdList(Long userId) {
        return ResponseResult.success(userFollowService.getFollowUserIdList(userId));
    }

    @RequestMapping("/isCurrentUserFollow")
    ResponseResult<Boolean> isCurrentUserFollow(Long userId) {
        return ResponseResult.success(userFollowService.isCurrentUserFollow(userId));
    }
}
