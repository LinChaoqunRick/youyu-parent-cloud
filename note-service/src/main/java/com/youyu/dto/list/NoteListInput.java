package com.youyu.dto.list;

import com.youyu.dto.common.PageBase;
import lombok.Data;

@Data
public class NoteListInput extends PageBase {
    private String name;
    private Long userId;
}
