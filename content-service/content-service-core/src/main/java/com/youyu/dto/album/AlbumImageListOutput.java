package com.youyu.dto.album;

import com.youyu.entity.album.AlbumImage;
import lombok.Data;

@Data
public class AlbumImageListOutput extends AlbumImage {
    private String url;
    private String originUrl;
}
