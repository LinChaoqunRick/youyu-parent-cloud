package com.youyu.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * (Visitor)表实体类
 *
 * @author makejava
 * @since 2025-09-16 17:22:51
 */
@Getter
@Setter
@TableName("bs_visitor")
public class Visitor extends Model<Visitor> {
    //主键
    @TableId
    private Long id;
    //昵称
    private String nickname;
    //头像
    private String avatar;
    //邮箱
    private String email;
    //主页
    private String homepage;
    //区域编码
    private Integer adcode;
    //创建时间
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE, updateStrategy = FieldStrategy.NOT_EMPTY)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    //删除
    private Integer deleted;
}

