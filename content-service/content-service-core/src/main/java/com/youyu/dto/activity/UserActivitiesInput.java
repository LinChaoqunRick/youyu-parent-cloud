package com.youyu.dto.activity;

import com.youyu.dto.page.PageBase;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class UserActivitiesInput extends PageBase {
    @NotNull(message = "用户id不能为空")
    private Long userId;
    /**
     * 当前登录用户，用于做水平越权检查
     */
    private Long authorizationUserId;
}