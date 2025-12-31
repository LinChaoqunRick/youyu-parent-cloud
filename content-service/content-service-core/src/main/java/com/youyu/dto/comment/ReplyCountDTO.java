package com.youyu.dto.comment;

import lombok.Data;

/**
 * 回复数量统计 DTO
 */
@Data
public class ReplyCountDTO {
    /**
     * 根评论ID
     */
    private Long rootId;

    /**
     * 回复数量
     */
    private Long replyCount;
}