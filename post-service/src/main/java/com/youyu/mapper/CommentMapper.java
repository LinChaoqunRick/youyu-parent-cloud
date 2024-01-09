package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.dto.post.comment.CommentListOutput;
import com.youyu.entity.post.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (Comment)表数据库访问层
 *
 * @author makejava
 * @since 2023-02-12 21:20:00
 */
@Mapper
@Repository
public interface CommentMapper extends BaseMapper<Comment> {
    List<CommentListOutput> getCommentCountByPostId(@Param("postId") Long postId);
}

