package com.youyu.service.activity;

import com.youyu.dto.activity.UserActivitiesInput;
import com.youyu.dto.page.PageOutput;

/**
 * 用户活动服务接口
 */
public interface UserActivityService {
    /**
     * 获取用户活动列表
     * @param input 查询条件
     * @return 用户活动分页数据
     */
    PageOutput<Object> listUserActivities(UserActivitiesInput input);
}