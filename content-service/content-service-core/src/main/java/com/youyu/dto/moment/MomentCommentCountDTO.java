package com.youyu.dto.moment;

import lombok.Data;

/**
 * 时刻评论数量统计 DTO
 */
@Data
public class MomentCommentCountDTO {
    /**
     * 时刻ID
     */
    private Long momentId;

    /**
     * 评论总数（包括根评论和子评论）
     */
    private Integer commentCount;
}