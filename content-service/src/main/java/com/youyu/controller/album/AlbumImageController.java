package com.youyu.controller.album;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.dto.album.AlbumImageListInput;
import com.youyu.dto.album.AlbumImageListOutput;
import com.youyu.dto.album.AlbumImageSaveInput;
import com.youyu.dto.common.PageOutput;
import com.youyu.entity.album.Album;
import com.youyu.entity.album.AlbumImage;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.result.ResponseResult;

import com.youyu.service.album.AlbumImageService;
import com.youyu.service.album.AlbumService;
import com.youyu.utils.PageUtils;
import com.youyu.utils.SecurityUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * (AlbumImage)表控制层
 *
 * @author makejava
 * @since 2024-06-03 21:06:18
 */
@RefreshScope
@Data
@ConfigurationProperties(prefix = "aliyun.oss")
@RestController
@RequestMapping("albumImage")
public class AlbumImageController {
    private String bucket;
    // 签名方式
    private String accessKeyId;
    private String accessKeySecret;
    private String endPoint;
    private String host;
    //STS方式
    private String roleArn;
    private String accessKeyIdRAM;
    private String accessKeySecretRAM;
    private String endPointRAM;

    @Resource
    private AlbumImageService albumImageService;

    @Resource
    private AlbumService albumService;

    @RequestMapping("/open/list")
    public ResponseResult<PageOutput<AlbumImageListOutput>> list(@Valid AlbumImageListInput input) {
        LambdaQueryWrapper<AlbumImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlbumImage::getAlbumId, input.getId());
        queryWrapper.orderByDesc(AlbumImage::getCreateTime);

        Album album = albumService.getById(input.getId());
        // 私密，且不是作者或授权用户
        if (album.getOpen() == 0 && !isAccessible(album, SecurityUtils.getUserId())) {
            throw new SystemException(ResultCode.FORBIDDEN);
        }

        Page<AlbumImage> page = new Page<>(input.getPageNum(), input.getPageSize());
        Page<AlbumImage> imagePage = albumImageService.page(page, queryWrapper);
        PageOutput<AlbumImageListOutput> pageOutput = PageUtils.setPageResult(imagePage, AlbumImageListOutput.class);

        // 生成url路径
        generateOSSUrl(pageOutput.getList());

        return ResponseResult.success(pageOutput);
    }

    @RequestMapping("/open/detail")
    public ResponseResult<AlbumImage> save(@RequestParam Long id) {
        AlbumImage albumImage = albumImageService.getById(id);
        return ResponseResult.success(albumImage);
    }

    @RequestMapping("/create")
    public ResponseResult<Boolean> create(@Valid @RequestBody AlbumImageSaveInput input) {
        Album album = albumService.getById(input.getAlbumId());
        SecurityUtils.authAuthorizationUser(album.getUserId());
        input.getImages().forEach(image -> image.setAlbumId(input.getAlbumId()));
        boolean save = albumImageService.saveBatch(input.getImages());
        return ResponseResult.success(save);
    }

    @RequestMapping("/update")
    public ResponseResult<Boolean> update(AlbumImage input) {
        Album album = albumService.getById(input.getAlbumId());
        SecurityUtils.authAuthorizationUser(album.getUserId());
        boolean update = albumImageService.updateById(input);
        return ResponseResult.success(update);
    }

    @RequestMapping("/remove")
    public ResponseResult<Boolean> remove(String ids) {
        //TODO... 如何进行水平越权校验？
        List<String> idsList = Arrays.stream(ids.split(",")).toList();
        boolean removed = albumImageService.removeBatchByIds(idsList);
        return ResponseResult.success(removed);
    }

    @RequestMapping("/open/origin")
    public ResponseResult<String> getOriginUrl(@RequestParam Long id) {
        AlbumImage albumImage = albumImageService.getById(id);
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);

        // 指定签名URL过期时间为10分钟。
        Date expiration = new Date(new Date().getTime() + 1000 * 60 * 10);
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucket, albumImage.getPath(), HttpMethod.GET);
        req.setExpiration(expiration);
        URL signedOriginalUrl = ossClient.generatePresignedUrl(req);
        return ResponseResult.success(signedOriginalUrl.toString());
    }

    public boolean isAccessible(Album album, Long userId) {
        if (Objects.equals(album.getUserId(), userId)) {
            return true;
        }
        List<String> numberList = Arrays.asList(album.getAuthorizedUsers().split(","));
        return numberList.contains(String.valueOf(userId));
    }

    /**
     * 生成oss的带权限的url
     *
     * @param imageList 图片列表;
     */
    public void generateOSSUrl(List<AlbumImageListOutput> imageList) {
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);

        imageList.forEach(item -> {
            // 指定签名URL过期时间为10分钟。
            Date expiration = new Date(new Date().getTime() + 1000 * 60 * 10);
            GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucket, item.getPath(), HttpMethod.GET);
            req.setExpiration(expiration);
//            URL signedOriginalUrl = ossClient.generatePresignedUrl(req);
//            item.setOriginUrl(signedOriginalUrl.toString());
            req.setProcess("style/thumbnail");
            URL signedUrl = ossClient.generatePresignedUrl(req);
            item.setUrl(signedUrl.toString());
        });

        // 关闭 OSS 客户端
        ossClient.shutdown();
    }
}

