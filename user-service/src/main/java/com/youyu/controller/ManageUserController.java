package com.youyu.controller;

import com.youyu.annotation.Log;
import com.youyu.entity.auth.Route;
import com.youyu.enums.BusinessType;
import com.youyu.result.ResponseResult;
import com.youyu.service.UserService;
import com.youyu.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/manage/user")
public class ManageUserController {
    @Resource
    private UserService userService;

    @RequestMapping("/getAuthRoutes")
    @Log(title = "获取权限路由", type = BusinessType.GET_ROUTER)
    public ResponseResult<List<Route>> getAuthRoutes() {
        Long userId = SecurityUtils.getUserId();
        List<Route> manageAuthRoutes = userService.getManageAuthRoutes(userId);
        return ResponseResult.success(manageAuthRoutes);
    }
}
