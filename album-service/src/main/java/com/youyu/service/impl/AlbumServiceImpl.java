package com.youyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.AlbumListOutput;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.post.post.PostUserExtraInfo;
import com.youyu.dto.post.post.PostUserOutput;
import com.youyu.entity.post.Post;
import com.youyu.entity.post.PostLike;
import com.youyu.entity.user.User;
import com.youyu.feign.UserServiceClient;
import com.youyu.mapper.AlbumMapper;
import com.youyu.entity.Album;
import com.youyu.service.AlbumService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.PageUtils;
import com.youyu.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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

    @Resource
    private UserServiceClient userServiceClient;

    @Override
    public PageOutput<AlbumListOutput> selectPage(Page<Album> page, String name) {
        Page<Album> albumPage = albumMapper.selectPage(page, name);

        // 封装查询结果
        PageOutput<AlbumListOutput> pageOutput = PageUtils.setPageResult(albumPage, AlbumListOutput.class);

        // 清除授权信息
        pageOutput.getList().forEach(item -> {
            item.setAuthorizedUserList(null);
            item.setAuthorizedUsers(null);
            PostUserOutput detail = getUserDetailById(item.getUserId());
            item.setUserInfo(detail);
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

