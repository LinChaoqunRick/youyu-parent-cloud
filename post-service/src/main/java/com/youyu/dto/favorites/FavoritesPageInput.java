package com.youyu.dto.favorites;

import com.youyu.dto.common.PageBase;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class FavoritesPageInput extends PageBase {
    @NotNull(message = "收藏夹id不能为空")
    private Long favoritesId;
}
