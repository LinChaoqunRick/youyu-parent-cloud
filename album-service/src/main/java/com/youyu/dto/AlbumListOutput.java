package com.youyu.dto;

import com.youyu.entity.Album;
import lombok.Data;

@Data
public class AlbumListOutput extends Album {
    private Long imageCount;
}
