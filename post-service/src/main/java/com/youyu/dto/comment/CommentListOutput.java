package com.youyu.dto.comment;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youyu.dto.post.PostUserOutput;
import com.youyu.entity.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentListOutput extends Comment {
    /**
     * 子评论数量
     */
    @TableField(exist = false)
    private Integer replyCount;

    /**
     * 发布人
     */
    @TableField(exist = false)
    private PostUserOutput user;

    /**
     * 回复人
     */
    @TableField(exist = false)
    private PostUserOutput userTo;
    /**
     * 当前用户点赞信息
     */
    @TableField(exist = false)
    private boolean commentLike;
}
