package com.youyu.dto.album;

import lombok.Data;

/**
 * 相册照片数量统计DTO
 */
@Data
public class AlbumImageCountDTO {
    /**
     * 相册ID
     */
    private Long albumId;

    /**
     * 照片数量
     */
    private Long imageCount;
}
