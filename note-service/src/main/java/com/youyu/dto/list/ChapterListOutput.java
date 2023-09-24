package com.youyu.dto.list;

import com.youyu.dto.detail.NoteUserOutput;
import com.youyu.entity.NoteChapter;
import lombok.Data;

@Data
public class ChapterListOutput extends NoteChapter {
    private NoteUserOutput user;
}
