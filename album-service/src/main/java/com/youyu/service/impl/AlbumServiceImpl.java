package com.youyu.service.impl;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.AlbumListOutput;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.post.post.PostUserOutput;
import com.youyu.entity.AlbumImage;
import com.youyu.entity.user.User;
import com.youyu.feign.UserServiceClient;
import com.youyu.mapper.AlbumMapper;
import com.youyu.entity.Album;
import com.youyu.service.AlbumImageService;
import com.youyu.service.AlbumService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.PageUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Date;
import java.util.Objects;

/**
 * (Album)表服务实现类
 *
 * @author makejava
 * @since 2024-06-02 13:49:21
 */
@RefreshScope
@Data
@Service("albumService")
@ConfigurationProperties(prefix = "aliyun.oss")
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album> implements AlbumService {
    private String bucket;
    // 签名方式
    private String accessKeyId;
    private String accessKeySecret;
    private String endPoint;
    private String host;

    @Resource
    private AlbumMapper albumMapper;

    @Resource
    private UserServiceClient userServiceClient;

    @Resource
    private AlbumImageService albumImageService;

    @Override
    public PageOutput<AlbumListOutput> selectPage(Page<Album> page, String name) {
        Page<Album> albumPage = albumMapper.selectPage(page, name);

        // 封装查询结果
        PageOutput<AlbumListOutput> pageOutput = PageUtils.setPageResult(albumPage, AlbumListOutput.class);

        // 生成ossClient
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);

        // 清除授权信息
        pageOutput.getList().forEach(item -> {
            item.setAuthorizedUserList(null);
            item.setAuthorizedUsers(null);
            PostUserOutput detail = getUserDetailById(item.getUserId());
            item.setUserInfo(detail);

            String cover = item.getCover();
            if (Objects.isNull(cover)) {
                // 如果没有设置封面，就取第一张照片
                LambdaQueryWrapper<AlbumImage> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(AlbumImage::getAlbumId, item.getId()).last("limit 1");
                AlbumImage fistImage = albumImageService.getOne(queryWrapper);
                if (fistImage != null) {
                    cover = fistImage.getPath();
                }
            }

            if (StringUtils.hasText(cover)) {
                // 指定签名URL过期时间为10分钟。
                Date expiration = new Date(new Date().getTime() + 1000 * 60 * 10);
                GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucket, cover, HttpMethod.GET);
                req.setExpiration(expiration);
                if (item.getOpen() == 1) {
                    req.setProcess("style/thumbnail");
                } else {
                    req.setProcess("style/blurred");
                }
                URL signedUrl = ossClient.generatePresignedUrl(req);
                item.setCover(signedUrl.toString());
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
        User user = userServiceClient.selectById(userId).getData();
        if (!Objects.isNull(user)) {
            return BeanCopyUtils.copyBean(user, PostUserOutput.class);
        } else {
            return null;
        }
    }
}

