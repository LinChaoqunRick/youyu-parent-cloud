package com.youyu.dto.post;

import lombok.Data;

/**
 * 文章评论数量统计DTO
 */
@Data
public class PostCommentCountDTO {
    /**
     * 文章ID
     */
    private Long postId;

    /**
     * 评论总数（包括根评论和子评论）
     */
    private Integer commentCount;
}
