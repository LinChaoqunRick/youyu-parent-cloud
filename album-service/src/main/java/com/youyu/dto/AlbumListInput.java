package com.youyu.dto;

import com.youyu.dto.common.PageBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlbumListInput extends PageBase {
    private String name;
    private Long userId;
}
