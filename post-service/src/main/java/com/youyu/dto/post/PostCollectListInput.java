package com.youyu.dto.post;

import com.youyu.dto.common.PageBase;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class PostCollectListInput extends PageBase {
    private Long userId;
}
