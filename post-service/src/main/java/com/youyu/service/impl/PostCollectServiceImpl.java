package com.youyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.post.post.PostCollectListInput;
import com.youyu.dto.post.post.PostListOutput;
import com.youyu.entity.post.Category;
import com.youyu.entity.post.Comment;
import com.youyu.entity.post.Post;
import com.youyu.entity.post.PostCollect;
import com.youyu.mapper.CategoryMapper;
import com.youyu.mapper.CommentMapper;
import com.youyu.mapper.PostCollectMapper;
import com.youyu.mapper.PostMapper;
import com.youyu.service.PostCollectService;
import com.youyu.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * (PostCollect)表服务实现类
 *
 * @author makejava
 * @since 2023-03-18 20:26:38
 */
@Service("postCollectService")
public class PostCollectServiceImpl extends ServiceImpl<PostCollectMapper, PostCollect> implements PostCollectService {

    @Autowired
    private PostCollectMapper postCollectMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public PageOutput<PostListOutput> getCollectList(PostCollectListInput input) {
        LambdaQueryWrapper<PostCollect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostCollect::getUserId, input.getUserId());
        List<PostCollect> list = postCollectMapper.selectList(queryWrapper);
        List<Long> postIds = list.stream()
                .map(item -> item.getPostId())
                .collect(Collectors.toList());

        if (postIds.size() == 0) {
            return new PageOutput<>();
        }

        LambdaQueryWrapper<Post> postLambdaQueryWrapper = new LambdaQueryWrapper<>();
        postLambdaQueryWrapper.select(Post.class, item -> !item.getColumn().equals("content"));
        postLambdaQueryWrapper.in(Post::getId, postIds);
        // 分页查询
        Page<Post> page = new Page<>(input.getPageNum(), input.getPageSize());
        Page<Post> postPage = postMapper.selectPage(page, postLambdaQueryWrapper);

        // 封装查询结果
        PageOutput<PostListOutput> pageOutput = PageUtils.setPageResult(postPage, PostListOutput.class);

        // 查询categoryName, 普通循环
        pageOutput.getList().forEach(post -> {
            PostListOutput output = (PostListOutput) post;
            Category category = categoryMapper.selectById(output.getCategoryId());
            if (!Objects.isNull(category)) {
                output.setCategoryName(category.getName());
            }

            // 查询评论数量
            LambdaQueryWrapper<Comment> commentQueryWrapper = new LambdaQueryWrapper<>();
            commentQueryWrapper.eq(Comment::getPostId, output.getId());
            Long commentCount = commentMapper.selectCount(commentQueryWrapper);
            output.setCommentCount(commentCount);
        });
        return pageOutput;
    }

    @Override
    public List<Long> getPostIdsByFavoriteId(Long favoriteId) {
        LambdaQueryWrapper<PostCollect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(PostCollect::getPostId).eq(PostCollect::getFavoritesId, favoriteId);
        List<PostCollect> collectList = postCollectMapper.selectList(queryWrapper);
        List<Long> postIds = collectList.stream().map(PostCollect::getPostId).collect(Collectors.toList());
        return postIds;
    }
}
