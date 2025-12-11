package com.youyu.dto.column;

import com.youyu.dto.post.PostUserOutput;
import com.youyu.entity.column.Column;
import lombok.Data;

@Data
public class ColumnListOutput extends Column {
    private Long postNum = 0L;
    private Long subscriberNum = 0L;
    private PostUserOutput user;
}
