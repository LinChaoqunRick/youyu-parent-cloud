package com.youyu.dto.note.list;

import com.youyu.dto.common.PageBase;
import lombok.Data;

@Data
public class NoteListInput extends PageBase {
    private String name;
    private Long userId;
}
