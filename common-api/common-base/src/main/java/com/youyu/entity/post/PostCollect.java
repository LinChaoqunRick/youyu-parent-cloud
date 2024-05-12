package com.youyu.entity.post;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * (PostCollect)实体类
 *
 * @author makejava
 * @since 2023-03-18 20:26:37
 */
@Data
@TableName("bs_post_collect")
public class PostCollect implements Serializable {
    private static final long serialVersionUID = 374151870360825444L;
    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 文章编号
     */
    @NotNull(message = "文章id不能为空")
    private Long postId;
    /**
     * 收藏夹编号
     */
    //@NotNull(message = "收藏夹id不能为空")
    private Long favoritesId;
    /**
     * 收藏人id
     */
    private Long userId;
    /**
     * 被收藏人id
     */
    private Long userIdTo;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE, updateStrategy = FieldStrategy.NOT_EMPTY)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}

