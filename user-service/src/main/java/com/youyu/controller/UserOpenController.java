package com.youyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.user.UserFansListInput;
import com.youyu.dto.user.UserFollowListInput;
import com.youyu.dto.user.UserListInput;
import com.youyu.dto.user.UserListOutput;
import com.youyu.entity.user.ProfileMenu;
import com.youyu.entity.user.User;
import com.youyu.result.ResponseResult;
import com.youyu.service.ProfileMenuService;
import com.youyu.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;

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
    private ProfileMenuService profileMenuService;

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

    @RequestMapping("/selectCount")
    public ResponseResult<Integer> selectCount(LambdaQueryWrapper<User> queryWrapper) {
        return ResponseResult.success(userService.count(queryWrapper));
    }

    @RequestMapping("/selectById")
    public ResponseResult<User> selectById(Long userId) {
        return ResponseResult.success(userService.getById(userId));
    }

    @RequestMapping("/selectPage")
    public ResponseResult<Page<User>> selectPage(Page<User> page, LambdaQueryWrapper<User> lambdaQueryWrapper) {
        return ResponseResult.success(userService.page(page, lambdaQueryWrapper));
    }
}

