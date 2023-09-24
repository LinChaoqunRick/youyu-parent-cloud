package com.youyu.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.io.Serializable;

/**
 * (NoteChapter)实体类
 *
 * @author makejava
 * @since 2023-04-08 22:49:24
 */
@Data
@TableName("bs_note_chapter")
public class NoteChapter implements Serializable {
    private static final long serialVersionUID = 886708716162865963L;

    @TableId
    private Long id;
    /**
     * 所属笔记
     */
    @NotNull(message = "所属笔记不能为空")
    private Long noteId;
    /**
     * 父目录
     */
    private Long parentId;
    /**
     * 创作人
     */
    @NotBlank(message = "创作人不能为空")
    private String userIds;
    /**
     * 章节标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;
    /**
     * 章节内容
     */
    private String content;
    /**
     * 浏览量
     */
    private Long viewCount;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, updateStrategy = FieldStrategy.NOT_EMPTY)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}

