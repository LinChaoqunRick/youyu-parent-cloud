package com.youyu.dto.analysis;

import lombok.Data;

/**
 * 评论数据
 */
@Data
public class CommentData {
    /**
     * 月份（格式：2024-01）
     */
    private String month;

    /**
     * 文章评论数量
     */
    private Long postCommentCount;

    /**
     * 时刻评论数量
     */
    private Long momentCommentCount;
}