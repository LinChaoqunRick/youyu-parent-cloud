package com.youyu.dto.note;

import lombok.Data;

/**
 * 笔记章节统计DTO
 */
@Data
public class NoteChapterStatsDTO {
    /**
     * 笔记ID
     */
    private Long noteId;

    /**
     * 章节数量
     */
    private Integer chapterCount;

    /**
     * 总浏览量
     */
    private Long totalViewCount;
}
