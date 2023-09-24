package com.youyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.detail.NoteDetailOutput;
import com.youyu.dto.detail.NoteUserOutput;
import com.youyu.dto.list.NoteListInput;
import com.youyu.dto.list.NoteListOutput;
import com.youyu.dto.post.PostUserOutput;
import com.youyu.entity.Note;

import java.util.List;

/**
 * (Note)表服务接口
 *
 * @author makejava
 * @since 2023-04-05 17:35:24
 */
public interface NoteService extends IService<Note> {
    PageOutput<NoteListOutput> noteList(NoteListInput input);

    NoteUserOutput getUserDetailById(Long userId, boolean enhance);

    List<NoteUserOutput> getUserDetailByIds(List<Long> userId, boolean enhance);

    NoteDetailOutput getNote(Long noteId);

    List<NoteListOutput> noteListByIds(List<Long> noteIds);
}
