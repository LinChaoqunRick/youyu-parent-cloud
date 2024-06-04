package com.youyu.dto;

import com.youyu.dto.common.PageBase;
import lombok.Data;

@Data
public class AlbumListInput extends PageBase {
    private String name = "";
}
