package com.youyu.dto.detail;

import com.youyu.entity.Note;
import com.youyu.entity.User;
import lombok.Data;

@Data
public class NoteDetailOutput extends Note {
    private long viewCount;
    private long subscribeCount;
    private int chapterCount;
    private NoteUserOutput user;
}
