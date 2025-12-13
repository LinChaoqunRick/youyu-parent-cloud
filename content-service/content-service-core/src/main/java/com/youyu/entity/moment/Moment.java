package com.youyu.entity.moment;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * (Moment)表实体类
 *
 * @author makejava
 * @since 2023-05-21 23:22:11
 */
@TableName("bs_moment")
@Data
public class Moment {
    //主键
    @TableId
    private Long id;
    //发表用户
    private Long userId;
    //内容
    @NotBlank(message = "内容不能为空")
    private String content;
    //此刻心情
    private Integer mood;
    //话题编号
    private Integer topicId;
    //图片
    private String images;
    //经度
    private String longitude;
    //维度
    private String latitude;
    //地址
    private String location;
    //区域编号
    private Integer adcode;
    //区域名称
    @TableField(exist = false)
    private String adname;
    //点赞数
    private Long supportCount;
    //反对数
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

