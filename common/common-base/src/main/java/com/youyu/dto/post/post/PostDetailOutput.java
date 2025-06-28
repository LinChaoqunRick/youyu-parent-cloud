package com.youyu.dto.post.post;

import com.youyu.dto.post.column.ColumnListOutput;
import com.youyu.entity.post.Post;
import lombok.Data;

import java.util.List;

@Data
public class PostDetailOutput extends Post {
    private boolean postLike;
    private boolean postCollect;
    private Long likeCount;
    private Long commentCount;
    private Long collectCount;
    private PostUserOutput user;
    private List<ColumnListOutput> columns;
}
