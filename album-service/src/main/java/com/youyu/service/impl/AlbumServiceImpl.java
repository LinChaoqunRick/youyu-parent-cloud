package com.youyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.AlbumListOutput;
import com.youyu.dto.common.PageOutput;
import com.youyu.mapper.AlbumMapper;
import com.youyu.entity.Album;
import com.youyu.service.AlbumService;
import com.youyu.utils.PageUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * (Album)表服务实现类
 *
 * @author makejava
 * @since 2024-06-02 13:49:21
 */
@Service("albumService")
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album> implements AlbumService {
    @Resource
    private AlbumMapper albumMapper;

    @Override
    public PageOutput<AlbumListOutput> selectPage(Page<Album> page, String name) {
        Page<Album> albumPage = albumMapper.selectPage(page, name);

        // 封装查询结果
        PageOutput<AlbumListOutput> pageOutput = PageUtils.setPageResult(albumPage, AlbumListOutput.class);
        return pageOutput;
    }
}

