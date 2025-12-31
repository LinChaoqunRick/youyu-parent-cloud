package com.youyu.mapper.post;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.dto.comment.CommentListOutput;
import com.youyu.dto.comment.ReplyCountDTO;
import com.youyu.dto.post.PostCommentCountDTO;
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

    /**
     * 批量查询根评论的回复数量
     * @param rootIds 根评论ID列表
     * @return 回复数量统计列表
     */
    List<ReplyCountDTO> batchGetReplyCount(@Param("rootIds") List<Long> rootIds);

    /**
     * 批量查询根评论的最新N条子评论
     * @param rootIds 根评论ID列表
     * @param limit 每个根评论返回的最大子评论数
     * @return 子评论列表
     */
    List<Comment> batchGetLatestReplies(@Param("rootIds") List<Long> rootIds, @Param("limit") int limit);

    /**
     * 批量查询文章的评论数量（包括根评论和子评论）
     * @param postIds 文章ID列表
     * @return 评论数量统计列表
     */
    List<PostCommentCountDTO> batchGetPostCommentCount(@Param("postIds") List<Long> postIds);
}

