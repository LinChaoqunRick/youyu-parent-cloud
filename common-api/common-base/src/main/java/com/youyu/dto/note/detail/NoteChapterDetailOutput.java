package com.youyu.dto.note.detail;

import com.youyu.entity.note.NoteChapter;
import lombok.Data;

import java.util.List;

@Data
public class NoteChapterDetailOutput extends NoteChapter {
    private List<NoteUserOutput> users;
}
