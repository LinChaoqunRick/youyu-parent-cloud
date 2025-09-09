package com.youyu.dto.user;

import com.youyu.dto.common.PageBase;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class UserActivitiesInput extends PageBase {
    @NotNull(message = "用户id不能用空")
    private Long userId;
    /**
     * 当前登录用户，用于做水平越权
     */
    private Long authorizationUserId;
}
