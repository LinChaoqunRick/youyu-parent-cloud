package com.youyu.mapper.album;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.dto.album.AlbumImageCountDTO;
import com.youyu.entity.album.AlbumImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (AlbumImage)表数据库访问层
 *
 * @author makejava
 * @since 2024-06-03 21:06:18
 */
@Mapper
@Repository
public interface AlbumImageMapper extends BaseMapper<AlbumImage> {

    /**
     * 批量查询相册的照片数量
     * @param albumIds 相册ID列表
     * @return 照片数量统计列表
     */
    List<AlbumImageCountDTO> batchGetImageCount(@Param("albumIds") List<Long> albumIds);

    /**
     * 批量查询每个相册的第一张照片
     * @param albumIds 相册ID列表
     * @return 每个相册的第一张照片列表
     */
    List<AlbumImage> batchGetFirstImages(@Param("albumIds") List<Long> albumIds);
}

