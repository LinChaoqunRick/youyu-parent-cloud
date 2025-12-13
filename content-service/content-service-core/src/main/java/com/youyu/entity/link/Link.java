package com.youyu.entity.link;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * (Link)表实体类
 *
 * @author makejava
 * @since 2025-09-11 15:26:59
 */
@Getter
@Setter
@TableName("bs_link")
public class Link extends Model<Link> {
    //主键
    @TableId
    private Long id;
    //名称
    private String name;
    //描述
    private String description;
    //头像
    private String avatar;
    //地址
    private String address;
    //状态
    private Integer status;

    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    //逻辑删除
    private Integer deleted;
}

