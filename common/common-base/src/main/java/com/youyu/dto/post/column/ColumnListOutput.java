package com.youyu.dto.post.column;

import com.youyu.dto.post.post.PostUserOutput;
import com.youyu.entity.user.Column;
import lombok.Data;

@Data
public class ColumnListOutput extends Column {
    private Long postNum = 0L;
    private Long subscriberNum = 0L;
    private PostUserOutput user;
}
