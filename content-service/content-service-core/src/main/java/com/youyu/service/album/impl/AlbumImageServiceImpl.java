package com.youyu.service.album.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.youyu.dto.album.AlbumImageCountDTO;
import com.youyu.entity.album.AlbumImage;
import com.youyu.mapper.album.AlbumImageMapper;
import com.youyu.service.album.AlbumImageService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * (AlbumImage)表服务实现类
 *
 * @author makejava
 * @since 2024-06-03 21:06:20
 */
@Service("albumImageService")
public class AlbumImageServiceImpl extends ServiceImpl<AlbumImageMapper, AlbumImage> implements AlbumImageService {

    @Resource
    private AlbumImageMapper albumImageMapper;

    @Override
    public Map<Long, Long> batchGetImageCount(List<Long> albumIds) {
        if (albumIds == null || albumIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<AlbumImageCountDTO> countList = albumImageMapper.batchGetImageCount(albumIds);
        return countList.stream()
                .collect(Collectors.toMap(AlbumImageCountDTO::getAlbumId, AlbumImageCountDTO::getImageCount));
    }

    @Override
    public Map<Long, AlbumImage> batchGetFirstImages(List<Long> albumIds) {
        if (albumIds == null || albumIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<AlbumImage> images = albumImageMapper.batchGetFirstImages(albumIds);
        return images.stream()
                .collect(Collectors.toMap(AlbumImage::getAlbumId, image -> image));
    }
}

