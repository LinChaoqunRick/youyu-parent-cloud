package com.youyu.service.album.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.UserDTO;
import com.youyu.dto.album.AlbumListOutput;
import com.youyu.dto.oss.OssBatchSignedUrlInput;
import com.youyu.dto.oss.OssSignedUrlInput;
import com.youyu.dto.page.PageOutput;
import com.youyu.dto.post.PostUserOutput;
import com.youyu.entity.album.Album;
import com.youyu.entity.album.AlbumImage;
import com.youyu.feign.OssServiceClient;
import com.youyu.feign.UserServiceClient;

import com.youyu.mapper.album.AlbumMapper;
import com.youyu.service.album.AlbumImageService;
import com.youyu.service.album.AlbumService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.PageUtils;
import lombok.Data;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * (Album)表服务实现类
 *
 * @author makejava
 * @since 2024-06-02 13:49:21
 */
@RefreshScope
@Data
@Service("albumService")
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album> implements AlbumService {

    @Resource
    private AlbumMapper albumMapper;

    @Resource
    private UserServiceClient userServiceClient;

    @Resource
    private AlbumImageService albumImageService;

    @Resource
    private OssServiceClient ossServiceClient;

    @Override
    public PageOutput<AlbumListOutput> selectPage(Page<Album> page, Album album) {
        Page<Album> albumPage = albumMapper.selectPage(page, new LambdaQueryWrapper<>(album));

        // 封装查询结果
        PageOutput<AlbumListOutput> pageOutput = PageUtils.setPageResult(albumPage, AlbumListOutput.class);

        if (pageOutput.getList().isEmpty()) {
            return pageOutput;
        }

        // 第一次遍历：收集封面路径和基本信息
        Map<String, AlbumListOutput> coverPathMap = new HashMap<>();
        List<String> openAlbumCovers = new ArrayList<>();
        List<String> privateAlbumCovers = new ArrayList<>();

        pageOutput.getList().forEach(item -> {
            // 清除授权信息
            item.setAuthorizedUserList(null);
            item.setAuthorizedUsers(null);

            // 获取用户信息
            PostUserOutput detail = getUserDetailById(item.getUserId());
            item.setUserInfo(detail);

            // 查询照片数量
            long imageCount = albumImageService.count(new LambdaQueryWrapper<AlbumImage>().eq(AlbumImage::getAlbumId, item.getId()));
            item.setImageCount(imageCount);

            // 设置封面路径
            Long coverImageId = item.getCoverImageId();
            String coverPath = null;

            if (Objects.nonNull(coverImageId)) {
                AlbumImage albumImage = albumImageService.getById(coverImageId);
                if (Objects.nonNull(albumImage)) {
                    coverPath = albumImage.getPath();
                }
            } else {
                // 如果没有设置封面，就取第一张照片
                LambdaQueryWrapper<AlbumImage> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(AlbumImage::getAlbumId, item.getId()).last("limit 1");
                AlbumImage firstImage = albumImageService.getOne(queryWrapper);
                if (firstImage != null) {
                    coverPath = firstImage.getPath();
                }
            }

            if (StringUtils.hasText(coverPath)) {
                coverPathMap.put(coverPath, item);
                if (item.getOpen() == 1) {
                    openAlbumCovers.add(coverPath);
                } else {
                    privateAlbumCovers.add(coverPath);
                }
            }
        });

        // 批量生成签名 URL
        Map<String, String> signedUrlMap = new HashMap<>();

        // 为公开相册生成缩略图 URL
        if (!openAlbumCovers.isEmpty()) {
            OssBatchSignedUrlInput openInput = new OssBatchSignedUrlInput();
            openInput.setPaths(openAlbumCovers);
            openInput.setExpireSeconds(600L); // 10分钟
            openInput.setProcess("style/thumbnail");
            openInput.setBucket("album"); // 使用相册 bucket
            Map<String, String> openUrls = ossServiceClient.generateBatchSignedUrl(openInput).getData();
            if (openUrls != null) {
                signedUrlMap.putAll(openUrls);
            }
        }

        // 为私密相册生成模糊图 URL
        if (!privateAlbumCovers.isEmpty()) {
            OssBatchSignedUrlInput privateInput = new OssBatchSignedUrlInput();
            privateInput.setPaths(privateAlbumCovers);
            privateInput.setExpireSeconds(600L); // 10分钟
            privateInput.setProcess("style/blurred");
            privateInput.setBucket("album"); // 使用相册 bucket
            Map<String, String> privateUrls = ossServiceClient.generateBatchSignedUrl(privateInput).getData();
            if (privateUrls != null) {
                signedUrlMap.putAll(privateUrls);
            }
        }

        // 第二次遍历：设置签名 URL
        coverPathMap.forEach((coverPath, item) -> {
            String signedUrl = signedUrlMap.get(coverPath);
            if (signedUrl != null) {
                item.setCover(signedUrl);
            }
        });

        return pageOutput;
    }

    /**
     * 获取用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    @Override
    public PostUserOutput getUserDetailById(Long userId) {
        if (Objects.isNull(userId)) {
            return null;
        }
        UserDTO user = userServiceClient.selectById(userId).getData();
        if (!Objects.isNull(user)) {
            return BeanCopyUtils.copyBean(user, PostUserOutput.class);
        } else {
            return null;
        }
    }
}

