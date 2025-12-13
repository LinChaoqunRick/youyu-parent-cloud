package com.youyu.mapper.activity;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.dto.activity.UserActivitiesInput;
import com.youyu.entity.activity.UserActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户活动 Mapper
 */
@Mapper
@Repository
public interface UserActivityMapper {
    IPage<UserActivity> listUserActivities(Page<?> page, @Param("input") UserActivitiesInput input);
}