package com.youyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.user.UserFansListInput;
import com.youyu.dto.user.UserFollowListInput;
import com.youyu.dto.user.UserListInput;
import com.youyu.dto.user.UserListOutput;
import com.youyu.entity.user.PositionInfo;
import com.youyu.entity.user.ProfileMenu;
import com.youyu.entity.user.User;
import com.youyu.entity.user.UserFollow;
import com.youyu.result.ResponseResult;
import com.youyu.service.ProfileMenuService;
import com.youyu.service.UserFollowService;
import com.youyu.service.UserService;
import com.youyu.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * (User)表控制层
 *
 * @author makejava
 * @since 2023-02-10 21:05:41
 */
@RestController
@RequestMapping("/user/open")
public class UserOpenController {

    @Resource
    private UserService userService;

    @Resource
    private UserFollowService userFollowService;

    @Resource
    private ProfileMenuService profileMenuService;

    @Resource
    private RestTemplate restTemplate;

    @Value("${amap.key}")
    private String amapKey;

    @RequestMapping("/list")
    ResponseResult<PageOutput<UserListOutput>> list(UserListInput input) {
        return ResponseResult.success(userService.list(input));
    }

    @RequestMapping("/getProfileMenu")
    ResponseResult<ProfileMenu> getProfileMenu(@RequestParam Long userId) {
        LambdaQueryWrapper<ProfileMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProfileMenu::getUserId, userId);
        ProfileMenu menu = profileMenuService.getOne(queryWrapper);
        if (Objects.isNull(menu)) {
            ProfileMenu defaultMenu = new ProfileMenu();
            defaultMenu.setUserId(userId);
            defaultMenu.setId(-1L);
            return ResponseResult.success(defaultMenu);
        }
        return ResponseResult.success(menu);
    }

    @RequestMapping("/followList")
    ResponseResult<PageOutput<UserListOutput>> followList(@Valid UserFollowListInput input) {
        return ResponseResult.success(userService.followList(input));
    }

    @RequestMapping("/fansList")
    ResponseResult<PageOutput<UserListOutput>> fansList(@Valid UserFansListInput input) {
        return ResponseResult.success(userService.fansList(input));
    }

    @RequestMapping("/selectCountByUserIdTo")
    public ResponseResult<Integer> selectCountByUserIdTo(Long userId) {
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getUserIdTo, userId);
        return ResponseResult.success(userFollowService.count(queryWrapper));
    }

    @RequestMapping("/selectById")
    public ResponseResult<User> selectById(Long userId) {
        return ResponseResult.success(userService.getById(userId));
    }

    @RequestMapping("/pageUserByUserIds")
    public ResponseResult<Page<User>> pageUserByUserIds(@RequestParam long current, @RequestParam long size, @RequestParam List<Long> userIds) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, userIds);
        Page<User> page = userService.page(new Page<>(current, size), queryWrapper);
        return ResponseResult.success(page);
    }

    @RequestMapping("/listByIds")
    public ResponseResult<List<User>> listByIds(List<Long> userIds) {
        return ResponseResult.success(userService.listByIds(userIds));
    }

    @RequestMapping("/getUserPositionInfo")
    public ResponseResult<PositionInfo> getUserPositionInfo() {
        PositionInfo positionInfo = restTemplate.getForObject("https://restapi.amap.com/v3/ip?key=" + amapKey + "&ip=" + RequestUtils.getClientIp(), PositionInfo.class);
        return ResponseResult.success(positionInfo);
    }
}

