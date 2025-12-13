package com.youyu.dto.album;

import com.youyu.entity.album.AlbumImage;
import lombok.Data;

import java.util.List;

@Data
public class AlbumImageSaveInput {
//    @NotNull(message = "相册id不能为空")
    private Long albumId;
    private List<AlbumImage> images;
}
