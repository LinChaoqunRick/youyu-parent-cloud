package com.youyu.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * (CommentLike)实体类
 *
 * @author makejava
 * @since 2023-03-06 21:50:32
 */
@Data
@TableName("bs_comment_like")
public class CommentLike implements Serializable {
    private static final long serialVersionUID = 659566992299306194L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 评论Id
     */
    @NotNull(message = "评论id不能为空")
    private Long commentId;
    /**
     * 点赞人
     */
    @NotNull(message = "点赞人不能为空")
    private Long userId;
    /**
     * 被点赞人
     */
    @NotNull(message = "被点赞人不能为空")
    private Long userIdTo;
    /**
     * 创建时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Date updateTime;

}

