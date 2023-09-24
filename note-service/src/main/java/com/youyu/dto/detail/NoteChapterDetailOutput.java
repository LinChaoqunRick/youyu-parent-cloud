package com.youyu.dto.detail;

import com.youyu.entity.NoteChapter;
import lombok.Data;

import java.util.List;

@Data
public class NoteChapterDetailOutput extends NoteChapter {
    private List<NoteUserOutput> users;
}
