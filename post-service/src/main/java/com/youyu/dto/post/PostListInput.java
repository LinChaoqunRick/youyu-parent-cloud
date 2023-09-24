package com.youyu.dto.post;

import com.youyu.dto.common.PageBase;
import lombok.Data;

@Data
public class PostListInput extends PageBase {
    public Long categoryId;
    public Long userId;
    public boolean original;
    public boolean sort;
    public String key;
}
