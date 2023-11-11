package com.youyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.note.detail.NoteChapterDetailOutput;
import com.youyu.dto.note.list.ChapterListOutput;
import com.youyu.entity.note.NoteChapter;

import java.util.List;

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
}
