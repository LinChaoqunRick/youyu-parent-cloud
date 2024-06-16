package com.youyu.dto;

import com.youyu.entity.AlbumImage;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AlbumImageSaveInput {
//    @NotNull(message = "相册id不能为空")
    private Long albumId;
    private List<AlbumImage> images;
}
