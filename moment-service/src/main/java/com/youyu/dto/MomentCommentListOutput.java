package com.youyu.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youyu.entity.MomentComment;
import com.youyu.entity.MomentCommentLike;
import com.youyu.entity.MomentUserOutput;
import com.youyu.entity.User;
import lombok.Data;

@Data
public class MomentCommentListOutput extends MomentComment {
    private Integer replyCount = 0;

    private MomentUserOutput user;

    private MomentUserOutput userTo;

    private boolean commentLike;
}
