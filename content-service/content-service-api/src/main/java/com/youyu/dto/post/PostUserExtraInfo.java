package com.youyu.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostUserExtraInfo {
    private Long postCount;
    private Long viewCount;
    private Long likeCount;
    private Long fansCount;
}
