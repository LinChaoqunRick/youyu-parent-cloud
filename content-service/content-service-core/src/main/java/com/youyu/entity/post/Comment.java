package com.youyu.entity.post;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * (Comment)实体类
 *
 * @author makejava
 * @since 2023-02-12 21:19:27
 */
@Getter
@Setter
@TableName("bs_comment")
public class Comment extends Model<Comment> {
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
    private Long userId;
    /**
     * 游客id
     */
    private Long visitorId;
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
     * 评论图片
     */
    private String images;
    /**
     * 区域编号
     */
    private Integer adcode;
    /**
     * 区域名称
     */
    @TableField(exist = false)
    private String adname;
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

    // 游客邮箱
    @TableField(exist = false)
    private String email;
    // 游客昵称
    @TableField(exist = false)
    private String nickname;
    // 游客主页
    @TableField(exist = false)
    private String homepage;
}

