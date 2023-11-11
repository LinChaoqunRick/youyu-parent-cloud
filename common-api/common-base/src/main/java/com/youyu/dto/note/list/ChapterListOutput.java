package com.youyu.dto.note.list;

import com.youyu.dto.note.detail.NoteUserOutput;
import com.youyu.entity.note.NoteChapter;
import lombok.Data;

@Data
public class ChapterListOutput extends NoteChapter {
    private NoteUserOutput user;
}
