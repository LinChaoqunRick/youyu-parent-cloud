package com.youyu.dto.post;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youyu.dto.column.ColumnListOutput;
import com.youyu.entity.Post;
import lombok.Data;

import java.util.List;

@Data
public class PostDetailOutput extends Post {
    private boolean postLike;
    private boolean postCollect;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    private List<ColumnListOutput> columns;
}
