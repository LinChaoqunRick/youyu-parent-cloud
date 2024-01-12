package com.youyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youyu.entity.user.UserFollow;
import com.youyu.mapper.UserFollowMapper;
import com.youyu.result.ResponseResult;
import com.youyu.service.UserFollowService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @RequestMapping("/getFansCount")
    ResponseResult<Integer> getFansCount(@RequestParam Long userId) {
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getUserIdTo, userId);
        return ResponseResult.success(userFollowMapper.selectCount(queryWrapper));
    }

    @RequestMapping("/isFollow")
    ResponseResult<Integer> isSubscribe(@RequestParam Long userId, @RequestParam Long userIdTo) {
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getUserId, userId);
        queryWrapper.eq(UserFollow::getUserIdTo, userIdTo);
        return ResponseResult.success(userFollowMapper.selectCount(queryWrapper));
    }

    @RequestMapping("/getFollowUserIdList")
    ResponseResult<List<Long>> getFollowUserIdList(@RequestParam Long userId) {
        return ResponseResult.success(userFollowService.getFollowUserIdList(userId));
    }

    @RequestMapping("/isCurrentUserFollow")
    ResponseResult<Boolean> isCurrentUserFollow(@RequestParam Long userId) {
        return ResponseResult.success(userFollowService.isCurrentUserFollow(userId));
    }

    @RequestMapping("/getUserFollowCount")
    public ResponseResult<Integer> getUserFollowCount(@RequestParam Long userId) {
        return ResponseResult.success(userFollowService.getUserFollowCount(userId));
    }
}
