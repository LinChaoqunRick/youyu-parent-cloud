package com.youyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youyu.entity.auth.Route;
import com.youyu.entity.auth.UserFramework;
import com.youyu.entity.user.ProfileMenu;
import com.youyu.entity.user.User;
import com.youyu.entity.user.UserDetailOutput;
import com.youyu.entity.user.UserFollow;
import com.youyu.enums.ResultCode;
import com.youyu.enums.RoleEnum;
import com.youyu.exception.SystemException;
import com.youyu.result.ResponseResult;
import com.youyu.service.ProfileMenuService;
import com.youyu.service.UserFollowService;
import com.youyu.service.UserService;
import com.youyu.utils.RedisCache;
import com.youyu.utils.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * (User)表控制层
 *
 * @author makejava
 * @since 2023-02-10 21:05:41
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserFollowService userFollowService;

    @Resource
    private ProfileMenuService profileMenuService;

    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    private RedisCache redisCache;

    @RequestMapping("/follow")
    ResponseResult<Boolean> userFollow(UserFollow input) {
        boolean save = userFollowService.save(input);
        return ResponseResult.success(save);
    }

    @RequestMapping("/unfollow")
    ResponseResult<Boolean> userUnFollow(UserFollow input) {
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getUserId, input.getUserId());
        queryWrapper.eq(UserFollow::getUserIdTo, input.getUserIdTo());
        boolean remove = userFollowService.remove(queryWrapper);
        return ResponseResult.success(remove);
    }

    @RequestMapping("/detail")
    ResponseResult<UserDetailOutput> detail(@RequestParam Long userId) {
        SecurityUtils.authContextUser(userId);
        UserDetailOutput detail = userService.detail(userId);
        return ResponseResult.success(detail);
    }

    @RequestMapping("/saveBasicInfo")
    ResponseResult<Boolean> saveBasicInfo(@Valid User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getNickname, user.getNickname());
        queryWrapper.ne(User::getId, user.getId());
        int count = userService.count(queryWrapper);
        if (count > 0) {
            throw new SystemException(ResultCode.NICKNAME_CONFLICT);
        }

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, user.getId());
        updateWrapper.set(User::getNickname, user.getNickname());
        updateWrapper.set(User::getSex, user.getSex());
        updateWrapper.set(User::getSignature, user.getSignature());
        updateWrapper.set(User::getBirthday, user.getBirthday());
        updateWrapper.set(User::getAvatar, user.getAvatar());
        return ResponseResult.success(userService.update(updateWrapper));
    }

    @RequestMapping("/saveHomepage")
    ResponseResult<Boolean> saveHomepage(@Valid User user) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, user.getId());
        updateWrapper.set(User::getHomepage, user.getHomepage());
        boolean result = userService.update(updateWrapper);
        if (!result) {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
        return ResponseResult.success(true);
    }

    @RequestMapping("/saveTelephone")
    ResponseResult<Boolean> saveHomepage(@RequestParam String oldTel, @RequestParam String newTel) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, newTel);
        int count = userService.count(queryWrapper);
        if (count > 0) {
            throw new SystemException(ResultCode.TELEPHONE_CONFLICT);
        }

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getUsername, oldTel);
        updateWrapper.set(User::getUsername, newTel);
        boolean result = userService.update(updateWrapper);
        if (!result) {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
        return ResponseResult.success(true);
    }

    @RequestMapping("/saveEmail")
    ResponseResult<Boolean> saveEmail(@RequestParam Long userId, @RequestParam String email, @RequestParam String code) {
        //从redis中获取用户信息
        String redisKey = "emailCode:" + email;
        String redisCode = redisCache.getCacheObject(redisKey);
        if (Objects.isNull(redisCode) || !redisCode.equals(code)) {
            throw new SystemException(700, "验证码错误或已过期");
        }
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, userId);
        updateWrapper.set(User::getEmail, email);
        boolean update = userService.update(updateWrapper);
        if (!update) {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
        return ResponseResult.success(true);
    }

    @RequestMapping("/savePassword")
    ResponseResult<Boolean> savePassword(@RequestParam String telephone, @RequestParam String password) {
        password = passwordEncoder.encode(password);
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getUsername, telephone);
        updateWrapper.set(User::getPassword, password);

        boolean result = userService.update(updateWrapper);
        if (!result) {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
        return ResponseResult.success(true);
    }

    @RequestMapping("/setProfileMenu")
    ResponseResult<Boolean> getProfileMenu(@Valid ProfileMenu profileMenu) {
        LambdaUpdateWrapper<ProfileMenu> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ProfileMenu::getUserId, profileMenu.getUserId());
        boolean saveOrUpdate = profileMenuService.saveOrUpdate(profileMenu, updateWrapper);
        return ResponseResult.success(saveOrUpdate);
    }

    @RequestMapping("/getCurrentUser")
    public ResponseResult<UserFramework> getCurrentUser() {
        Long currentUserId = SecurityUtils.getUserId();
        UserFramework user;
        if (Objects.nonNull(currentUserId)) {
            user = userService.getUserById(currentUserId);
        } else {
            throw new SystemException(ResultCode.USER_NOT_EXIST);
        }
        return ResponseResult.success(user);
    }

    @RequestMapping("/getAuthRoutes")
    public ResponseResult<List<Route>> getAuthRoutes() {
        String token = SecurityUtils.getAuthorizationToken();
        if (StringUtils.hasText(token)) {
            return ResponseResult.success(userService.getAuthRoutes(SecurityUtils.getUserId()));
        } else {
            return ResponseResult.success(userService.getRoutesByRoleId(RoleEnum.NO_LOGGED_USER.getId()));
        }
    }
}

