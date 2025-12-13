package com.youyu.mapper.album;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.album.AlbumImage;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (AlbumImage)表数据库访问层
 *
 * @author makejava
 * @since 2024-06-03 21:06:18
 */
@Mapper
@Repository
public interface AlbumImageMapper extends BaseMapper<AlbumImage> {

}

