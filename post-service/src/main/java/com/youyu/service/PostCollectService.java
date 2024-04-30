package com.youyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.post.post.PostCollectListInput;
import com.youyu.dto.post.post.PostListOutput;
import com.youyu.entity.post.PostCollect;

import java.util.List;


/**
 * (PostCollect)表服务接口
 *
 * @author makejava
 * @since 2023-03-18 20:26:38
 */
public interface PostCollectService extends IService<PostCollect> {
    PageOutput<PostListOutput> getCollectList(PostCollectListInput input);
    List<Long> getPostIdsByFavoriteId(Long favoriteId);
}
