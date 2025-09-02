package com.youyu.controller;

import com.youyu.annotation.Log;
import com.youyu.entity.auth.Route;
import com.youyu.entity.user.ManageUser;
import com.youyu.enums.LogType;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.result.ResponseResult;
import com.youyu.service.ManageUserService;
import com.youyu.service.UserService;
import com.youyu.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/manage/user")
public class ManageUserController {
    @Resource
    private UserService userService;

    @Resource
    private ManageUserService manageUserService;

    @RequestMapping("/getAuthRoutes")
    @Log(title = "获取权限路由", type = LogType.ACCESS)
    public ResponseResult<List<Route>> getAuthRoutes() {
        Long userId = SecurityUtils.getUserId();
        List<Route> manageAuthRoutes = userService.getManageAuthRoutes(userId);
        return ResponseResult.success(manageAuthRoutes);
    }

    @RequestMapping("/me")
    public ResponseResult<ManageUser> me() {
        Long currentUserId = SecurityUtils.getUserId();
        ManageUser user;
        if (Objects.nonNull(currentUserId)) {
            user = manageUserService.getById(currentUserId);
        } else {
            throw new SystemException(ResultCode.USER_NOT_EXIST);
        }
        return ResponseResult.success(user);
    }
}
