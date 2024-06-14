package com.youyu.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.AlbumListInput;
import com.youyu.dto.AlbumListOutput;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.post.post.PostUserOutput;
import com.youyu.entity.Album;

/**
 * (Album)表服务接口
 *
 * @author makejava
 * @since 2024-06-02 13:49:20
 */
public interface AlbumService extends IService<Album> {
    PageOutput<AlbumListOutput> selectPage(Page<Album> page, String name);
    PostUserOutput getUserDetailById(Long userId);
}

