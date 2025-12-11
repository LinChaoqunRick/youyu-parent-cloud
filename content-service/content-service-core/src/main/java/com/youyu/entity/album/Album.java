package com.youyu.entity.album;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


/**
 * (Album)表实体类
 *
 * @author makejava
 * @since 2024-06-02 13:49:18
 */
@TableName("bs_album")
@Data
public class Album extends Model<Album> {
    //主键
    @TableId
    private Long id;
    //相册名称
    @NotBlank(message = "相册名称不能为空")
    private String name;
    //所属用户
    private Long userId;
    // 授权用户
    private String authorizedUsers;
    //封面图片id
    private Long coverImageId;
    //封面
    private String cover;
    //相册描述
    @NotBlank(message = "相册描述不能为空")
    private String content;
    //订阅数量
    private Long subscribeCount;
    //点赞数量
    private Long likeCount;
    //是否开放
    @NotNull(message = "是否开放不能为空")
    private Integer open;

    //创建时间
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE, updateStrategy = FieldStrategy.NOT_EMPTY)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    //逻辑删除
    private Integer deleted;
}
