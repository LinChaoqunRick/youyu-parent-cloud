package com.youyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.annotation.Log;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.user.*;
import com.youyu.entity.auth.Route;
import com.youyu.entity.user.ProfileMenu;
import com.youyu.entity.user.User;
import com.youyu.enums.LogType;
import com.youyu.enums.RoleEnum;
import com.youyu.result.ResponseResult;
import com.youyu.service.ProfileMenuService;
import com.youyu.service.UserService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.LocateUtils;
import com.youyu.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.*;

/**
 * (User)表控制层
 *
 * @author makejava
 * @since 2023-02-10 21:05:41
 */
@Slf4j
@RestController
@RequestMapping("/user/open")
public class UserOpenController {

    @Resource
    private UserService userService;

    @Resource
    private ProfileMenuService profileMenuService;

    @Resource
    private LocateUtils locateUtils;

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

    @RequestMapping("/listUserActivities")
    ResponseResult<PageOutput<Object>> listUserActivities(@Valid UserActivitiesInput input) {
        input.setAuthorizationUserId(SecurityUtils.getUserId());
        PageOutput<Object> pageInfo = userService.listUserActivities(input);
        return ResponseResult.success(pageInfo);
    }

    @RequestMapping("/followList")
    ResponseResult<PageOutput<UserListOutput>> followList(@Valid UserFollowListInput input) {
        return ResponseResult.success(userService.followList(input));
    }

    @RequestMapping("/fansList")
    ResponseResult<PageOutput<UserListOutput>> fansList(@Valid UserFansListInput input) {
        return ResponseResult.success(userService.fansList(input));
    }

    @RequestMapping("/selectCountByUsername")
    public ResponseResult<Long> selectCountByUsername(@RequestParam String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return ResponseResult.success(userService.count(queryWrapper));
    }

    @RequestMapping("/selectCountByEmail")
    public ResponseResult<Long> selectCountByEmail(@RequestParam String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        return ResponseResult.success(userService.count(queryWrapper));
    }

    @RequestMapping("/selectById")
    public ResponseResult<User> selectById(@RequestParam Long userId) {
        User user = userService.getById(userId);
        user.setAdname(LocateUtils.getShortNameByCode(String.valueOf(user.getAdcode())));
        return ResponseResult.success(user);
    }

    @RequestMapping("/getUserBasicById")
    public ResponseResult<UserListOutput> getUserBasic(@RequestParam Long userId) {
        User user = userService.getById(userId);
        UserListOutput output = BeanCopyUtils.copyBean(user, UserListOutput.class);
        return ResponseResult.success(output);
    }

    @RequestMapping("/pageUserByUserIds")
    public ResponseResult<Page<User>> pageUserByUserIds(@RequestParam long current, @RequestParam long size, @RequestParam List<Long> userIds) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, userIds);
        Page<User> page = userService.page(new Page<>(current, size), queryWrapper);
        return ResponseResult.success(page);
    }

    @RequestMapping("/listByIds")
    public ResponseResult<List<User>> listByIds(@RequestParam("userIds") List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return ResponseResult.success(new ArrayList<>());
        }
        return ResponseResult.success(userService.listByIds(userIds));
    }

    // TODO... 新建文件放置此接口
//    @RequestMapping("/ipLocation")
//    public ResponseResult<TencentLocationResult> getUserPositionInfo() {
//        TencentLocationResult tencentLocationResult = locateUtils.queryTencentIp();
//        // log.info("当前访问IP:{}{}", RequestUtils.getClientIp(), tencentLocationResult.toString());
//        return ResponseResult.success(tencentLocationResult);
//    }

    @RequestMapping("/getAuthRoutes")
    @Log(title = "获取权限路由", type = LogType.ACCESS)
    public ResponseResult<List<Route>> getAuthRoutes() {
        Long userId = SecurityUtils.getUserId();
        if (Objects.isNull(userId)) {
            return ResponseResult.success(userService.getRoutesByRoleId(RoleEnum.NO_LOGGED_USER.getId()));
        } else {
            return ResponseResult.success(userService.getAuthRoutes(userId));
        }
    }
}

