package com.youyu.service.album.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.youyu.entity.album.AlbumImage;
import com.youyu.mapper.album.AlbumImageMapper;
import com.youyu.service.album.AlbumImageService;
import org.springframework.stereotype.Service;

/**
 * (AlbumImage)表服务实现类
 *
 * @author makejava
 * @since 2024-06-03 21:06:20
 */
@Service("albumImageService")
public class AlbumImageServiceImpl extends ServiceImpl<AlbumImageMapper, AlbumImage> implements AlbumImageService {

}

