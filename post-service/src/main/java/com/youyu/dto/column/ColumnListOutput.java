package com.youyu.dto.column;

import com.youyu.dto.post.PostUserOutput;
import com.youyu.entity.Column;
import lombok.Data;

@Data
public class ColumnListOutput extends Column {
    private Integer postNum = 0;
    private Integer subscriberNum = 0;
    private PostUserOutput user;
}
