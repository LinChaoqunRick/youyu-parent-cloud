package com.youyu.dto.album;

import com.youyu.dto.common.PageBase;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class AlbumImageListInput extends PageBase {
    @NotNull(message = "相册id不能为空")
    private Long id;
}
