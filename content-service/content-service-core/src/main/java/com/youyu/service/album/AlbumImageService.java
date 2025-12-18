package com.youyu.service.album;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.entity.album.AlbumImage;

import java.util.List;
import java.util.Map;

/**
 * (AlbumImage)表服务接口
 *
 * @author makejava
 * @since 2024-06-03 21:06:20
 */
public interface AlbumImageService extends IService<AlbumImage> {

    /**
     * 批量查询相册的照片数量
     * @param albumIds 相册ID列表
     * @return albumId -> 照片数量的映射
     */
    Map<Long, Long> batchGetImageCount(List<Long> albumIds);

    /**
     * 批量查询每个相册的第一张照片
     * @param albumIds 相册ID列表
     * @return albumId -> 第一张照片的映射
     */
    Map<Long, AlbumImage> batchGetFirstImages(List<Long> albumIds);
}

