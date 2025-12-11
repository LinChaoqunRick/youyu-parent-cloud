package com.youyu.dto.post;

import com.youyu.dto.page.PageBase;
import lombok.Data;

@Data
public class PostCollectListInput extends PageBase {
    private Long userId;
}
