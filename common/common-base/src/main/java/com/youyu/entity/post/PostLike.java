package com.youyu.entity.post;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * (PostLike)实体类
 *
 * @author makejava
 * @since 2023-02-19 17:01:44
 */
@TableName("bs_post_like")
@Data
@Component
public class PostLike implements Serializable {
    private static final long serialVersionUID = 508442806277887786L;
    /**
     * 点赞表主键
     */
    @TableId
    private Long id;
    /**
     * 帖子id
     */
    @NotNull(message = "文章id不能为空")
    private Long postId;
    /**
     * 收藏人
     */
    @NotNull(message = "用户id不能为空")
    private Long userId;
    /**
     * 被收藏人
     */
    private Long userIdTo;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, updateStrategy = FieldStrategy.NOT_EMPTY)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}

