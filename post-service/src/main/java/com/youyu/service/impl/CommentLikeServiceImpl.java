package com.youyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.entity.post.Comment;
import com.youyu.entity.post.CommentLike;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.mapper.CommentLikeMapper;
import com.youyu.mapper.CommentMapper;
import com.youyu.service.CommentLikeService;
import com.youyu.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * (CommentLike)表服务实现类
 *
 * @author makejava
 * @since 2023-03-06 21:51:09
 */
@Service("commentLikeService")
@Slf4j
public class CommentLikeServiceImpl extends ServiceImpl<CommentLikeMapper, CommentLike> implements CommentLikeService {

    @Resource
    private CommentLikeMapper commentLikeMapper;

    @Lazy
    @Resource
    private CommentMapper commentMapper;

    @Override
    @Transactional
    public boolean setPostCommentLike(CommentLike commentLike) {
        int insert = commentLikeMapper.insert(commentLike);
        if (insert > 0) {
            Comment comment = commentMapper.selectById(commentLike.getCommentId());
            comment.setSupportCount(comment.getSupportCount() + 1);
            commentMapper.updateById(comment);
            return true;
        } else {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
    }

    @Override
    public boolean isPostCommentLike(CommentLike commentLike) {
        LambdaQueryWrapper<CommentLike> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CommentLike::getUserId, SecurityUtils.getUserId());
        lambdaQueryWrapper.eq(CommentLike::getCommentId, commentLike.getCommentId());
        Integer count = commentLikeMapper.selectCount(lambdaQueryWrapper);
        return count > 0;
    }

    @Override
    @Transactional
    public boolean cancelPostCommentLike(CommentLike commentLike) {
        LambdaQueryWrapper<CommentLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommentLike::getCommentId, commentLike.getCommentId());
        queryWrapper.eq(CommentLike::getUserId, commentLike.getUserId());
        int delete = commentLikeMapper.delete(queryWrapper);
        if (delete > 0) {
            Comment comment = commentMapper.selectById(commentLike.getCommentId());
            comment.setSupportCount(comment.getSupportCount() - 1);
            commentMapper.updateById(comment);
            return true;
        } else {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
    }

    @Override
    public int getSupportCount(Long commentId) {
        LambdaQueryWrapper<CommentLike> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CommentLike::getCommentId, commentId);
        return commentLikeMapper.selectCount(lambdaQueryWrapper);
    }

    @Override
    public void rectifySupportCount() {
        List<Comment> commentList = commentMapper.selectList(null);
        commentList.forEach(comment -> {
            LambdaQueryWrapper<CommentLike> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CommentLike::getCommentId, comment.getId());
            Integer count = commentLikeMapper.selectCount(queryWrapper);
            comment.setSupportCount(count);
            commentMapper.updateById(comment);
        });
    }
}
