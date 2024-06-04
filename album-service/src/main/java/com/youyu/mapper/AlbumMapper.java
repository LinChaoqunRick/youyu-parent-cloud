package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.entity.Album;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (Album)表数据库访问层
 *
 * @author makejava
 * @since 2024-06-02 13:49:14
 */
@Mapper
@Repository
public interface AlbumMapper extends BaseMapper<Album> {
    Page<Album> selectPage(Page<Album> page, String name);
}

