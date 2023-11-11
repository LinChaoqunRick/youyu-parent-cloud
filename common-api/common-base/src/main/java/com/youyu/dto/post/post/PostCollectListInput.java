package com.youyu.dto.post.post;

import com.youyu.dto.common.PageBase;
import lombok.Data;

@Data
public class PostCollectListInput extends PageBase {
    private Long userId;
}
