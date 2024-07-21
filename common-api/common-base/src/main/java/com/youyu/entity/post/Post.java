package com.youyu.entity.post;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * (Post)表实体类
 *
 * @author makejava
 * @since 2023-01-01 16:28:29
 */
@SuppressWarnings("serial")
@TableName("bs_post")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class Post extends Model<Post> {
    @TableId
    private Long id;

    @NotNull(message = "用户不能为空")
    private Long userId;
    //标题
    @NotBlank(message = "标题不能为空")
//    @Size(min = 1,max = 100,message = "标题长度要在1—100个字符")
    private String title;
    //文章摘要
    @NotBlank(message = "摘要不能为空")
    private String summary;
    //文章内容
    @NotBlank(message = "内容不能为空")
    private String content;
    //文章类型：1文章 2草稿
    private String type;
    //
    @NotBlank(message = "文章类型不能为空")
    private String createType;
    @TableField(exist = false)
    private String createTypeDesc;

    private String tags;
    //所属分类Id
    @NotNull(message = "文章分类不能为空")
    private Long categoryId;

    @TableField(exist = false)
    private String categoryName;
    //缩略图地址
    @NotBlank(message = "至少上传一张封面")
    private String thumbnail;
    //专栏编号
    private String columnIds;
    //是否置顶（0 否 1 是）
    private String isTop;
    //状态（0已发布 1草稿）
    private Integer status;
    //访问量
    private Integer viewCount;
    //是否允许评论（0否 1是）
    private String isComment;

    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE, updateStrategy = FieldStrategy.NOT_EMPTY)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    //原文链接
    private String originalLink;
    //删除标志
    private Integer deleted;
}

