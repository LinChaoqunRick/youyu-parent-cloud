package com.youyu.dto.user;

import com.youyu.dto.page.PageBase;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class UserFollowListInput extends PageBase {
    @NotNull(message = "用户Id不能为空")
    private Long userId;
}
