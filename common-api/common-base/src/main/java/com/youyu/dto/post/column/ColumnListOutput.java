package com.youyu.dto.post.column;

import com.youyu.dto.post.post.PostUserOutput;
import com.youyu.entity.user.Column;
import lombok.Data;

@Data
public class ColumnListOutput extends Column {
    private Integer postNum = 0;
    private Integer subscriberNum = 0;
    private PostUserOutput user;
}
