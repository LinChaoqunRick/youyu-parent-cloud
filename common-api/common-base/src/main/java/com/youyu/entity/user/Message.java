package com.youyu.entity.user;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;


/**
 * (Message)表实体类
 *
 * @author makejava
 * @since 2024-02-20 22:04:01
 */
@EqualsAndHashCode(callSuper = true)
@TableName("bs_message")
@Data
public class Message extends Model<Message> {
    //留言主键
    @TableId
    private Long id;

    private Long rootId;

    private Long userId;

    private Long userIdTo;

    private String nickname;

    private String email;

    private String avatar;

    @NotBlank(message = "内容不能为空")
    private String content;
    //省份编号
    private Integer adcode;

    private Long supportCount;

    private Long opposeCount;

    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE, updateStrategy = FieldStrategy.NOT_EMPTY)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    //删除标志
    private Integer deleted;
}

