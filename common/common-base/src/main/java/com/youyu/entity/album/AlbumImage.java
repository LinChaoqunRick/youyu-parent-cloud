package com.youyu.entity.album;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * (AlbumImage)表实体类
 *
 * @author makejava
 * @since 2024-06-03 21:06:20
 */
@SuppressWarnings("serial")
@TableName("bs_album_image")
@Data
public class AlbumImage extends Model<AlbumImage> {
    //主键
    @TableId
    private Long id;
    //所属相册
    private Long albumId;
    //图片名称
    private String name;
    //图片路径
    private String path;
    //图片描述
    private String content;
    //图片大小
    private Long size;
    //是否开放
    private Integer open;
    //点赞数量
    private Long likeCount;
    //创建时间
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE, updateStrategy = FieldStrategy.NOT_EMPTY)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    //逻辑删除
    private Integer deleted;

}

