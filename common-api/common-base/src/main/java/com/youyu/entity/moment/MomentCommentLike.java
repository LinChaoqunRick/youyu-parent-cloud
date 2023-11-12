package com.youyu.entity.moment;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * (MomentCommentLike)表实体类
 *
 * @author makejava
 * @since 2023-07-10 22:25:25
 */
@SuppressWarnings("serial")
@Data
@TableName("bs_moment_comment_like")
public class MomentCommentLike extends Model<MomentCommentLike> {
    //主键
    @TableId
    private Long id;
    //时刻Id
    @NotNull(message = "评论id不能为空")
    private Long commentId;
    //点赞人
    private Long userId;
    //被点赞人
    private Long userIdTo;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE, updateStrategy = FieldStrategy.NOT_EMPTY)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}

