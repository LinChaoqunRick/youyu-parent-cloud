package com.youyu.dto;

import com.youyu.dto.page.PageBase;
import lombok.Data;

import jakarta.validation.constraints.NotNull;


@Data
public class FavoritesPageInput extends PageBase {
    @NotNull(message = "收藏夹id不能为空")
    private Long favoritesId;
}
