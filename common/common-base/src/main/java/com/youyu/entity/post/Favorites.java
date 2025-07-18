package com.youyu.entity.post;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * (Favorites)表实体类
 *
 * @author makejava
 * @since 2024-04-28 22:40:14
 */
@SuppressWarnings("serial")
@Data
@TableName("bs_favorites")
public class Favorites extends Model<Favorites> {
    //主键
    @TableId
    private Long id;
    //归属用户id
    private Long userId;
    //收藏夹名称
    @NotBlank(message = "收藏夹名称不能为空")
    private String name;
    //封面地址
    private String cover = "";
    //是否公开，1：是，0否
    private Integer open = 0;
    //是否置顶
    private Integer isTop = 0;
    //点赞数量
    private Long likeCount = 0L;
    //创建时间
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    //更新时间
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Date updateTime;
    //删除标志
    private Integer deleted = 0;
}

