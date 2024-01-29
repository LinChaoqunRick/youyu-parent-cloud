package com.youyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.post.post.PostDetailOutput;
import com.youyu.dto.post.post.PostListInput;
import com.youyu.dto.post.post.PostListOutput;
import com.youyu.dto.post.post.PostUserOutput;
import com.youyu.entity.post.Post;

import java.util.List;


/**
 * (Post)表服务接口
 *
 * @author makejava
 * @since 2023-01-01 16:28:29
 */
public interface PostService extends IService<Post> {
    PostUserOutput getUserDetailById(Long userId, boolean enhance);
    PageOutput<PostListOutput> postList(PostListInput input);
    List<PostListOutput> postListByIds(List<Long> ids);
    PostDetailOutput get(Long postId);
    Long getCommentCount(Long postId);
    Long getLikeCount(Long postId);
    Long getCollectCount(Long postId);
    void setPostListData(PostListOutput post);
}

