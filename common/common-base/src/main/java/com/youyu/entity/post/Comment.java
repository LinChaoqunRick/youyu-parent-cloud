package com.youyu.entity.post;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * (Comment)实体类
 *
 * @author makejava
 * @since 2023-02-12 21:19:27
 */
@Data
@TableName("bs_comment")
public class Comment implements Serializable {
    private static final long serialVersionUID = -68765964616647073L;

    private Long id;
    /**
     * 文章id
     */
    @NotNull(message = "文章id不能为空")
    private Long postId;
    /**
     * 根评论id
     */
    private Long rootId;
    /**
     * 用户id
     */
    @NotNull(message = "用户不能为空")
    private Long userId;
    /**
     * 被回复的用户id
     */
    @NotNull(message = "被回复人不能为空")
    private Long userIdTo;
    /**
     * 被回复的评论id
     */
    private Long replyId;
    /**
     * 评论内容
     */
    @NotBlank(message = "内容不能为空")
    private String content;
    /**
     * 支持数量
     */
    private Long supportCount;
    /**
     * 反对数量
     */
    private Long opposeCount;
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
    /**
     * 删除标志
     */
    private Integer deleted;

}

