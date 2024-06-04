package com.youyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.mapper.AlbumImageMapper;
import com.youyu.entity.AlbumImage;
import com.youyu.service.AlbumImageService;
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

