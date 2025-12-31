package com.youyu.service.note;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.note.ChapterListOutput;
import com.youyu.dto.note.NoteChapterStatsDTO;
import com.youyu.dto.note.detail.NoteChapterDetailOutput;
import com.youyu.entity.note.NoteChapter;

import java.util.List;
import java.util.Map;

/**
 * (NoteChapter)表服务接口
 *
 * @author makejava
 * @since 2023-04-08 22:49:24
 */
public interface NoteChapterService extends IService<NoteChapter> {
    List<NoteChapter> listChapter(Long noteId);

    List<ChapterListOutput> listChapterByIds(List<Long> noteIds);

    NoteChapterDetailOutput getChapter(Long id);

    /**
     * 批量查询笔记的章节统计信息
     * @param noteIds 笔记ID列表
     * @return noteId -> 章节统计信息的映射
     */
    Map<Long, NoteChapterStatsDTO> batchGetChapterStats(List<Long> noteIds);
}
