package com.youyu.dto;

import com.youyu.entity.AlbumImage;
import lombok.Data;

@Data
public class AlbumImageListOutput extends AlbumImage {
    private String url;
}
