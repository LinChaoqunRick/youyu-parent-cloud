package com.youyu.controller.activity;

import com.youyu.dto.activity.UserActivitiesInput;
import com.youyu.dto.page.PageOutput;
import com.youyu.result.ResponseResult;
import com.youyu.service.activity.UserActivityService;
import com.youyu.utils.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户活动控制器
 */
@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Resource
    private UserActivityService userActivityService;

    /**
     * 获取用户动态
     *
     * @param input 查询条件
     * @return 动态分页列表
     */
    @RequestMapping("/open/listUserActivities")
    public ResponseResult<PageOutput<Object>> listUserActivities(@Valid UserActivitiesInput input) {
        input.setAuthorizationUserId(SecurityUtils.getUserId());
        PageOutput<Object> pageInfo = userActivityService.listUserActivities(input);
        return ResponseResult.success(pageInfo);
    }
}