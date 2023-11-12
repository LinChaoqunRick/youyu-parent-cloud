package com.youyu.dto.note.detail;

import com.youyu.entity.note.Note;
import lombok.Data;

@Data
public class NoteDetailOutput extends Note {
    private long viewCount;
    private long subscribeCount;
    private int chapterCount;
    private NoteUserOutput user;
}
