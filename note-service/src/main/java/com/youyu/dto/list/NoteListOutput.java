package com.youyu.dto.list;

import com.youyu.dto.detail.NoteUserOutput;
import com.youyu.entity.Note;
import lombok.Data;

@Data
public class NoteListOutput extends Note {
    private Long viewCount = 0L;
    private Long subscribeCount = 0L;
    private Integer chapterCount = 0;
    private NoteUserOutput user;
}
