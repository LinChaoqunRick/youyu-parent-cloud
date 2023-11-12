package com.youyu.entity.note;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * (Note)实体类
 *
 * @author makejava
 * @since 2023-04-05 17:35:24
 */
@Data
@TableName("bs_note")
public class Note extends Model<Note> implements Serializable {
    private static final long serialVersionUID = 651760527440984859L;

    @TableId
    private Long id;
    /**
     * 笔记标题
     */
    @NotBlank(message = "标题不能为空")
    private String name;
    /**
     * 所属用户
     */
    @NotNull(message = "所属用户不能为空")
    private Long userId;
    /**
     * 笔记说明
     */
    @NotBlank(message = "说明不能为空")
    private String introduce;
    /**
     * 封面
     */
    private String cover;
    /**
     * 类型
     */
    private String type;
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

