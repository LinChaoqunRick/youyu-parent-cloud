package com.youyu.entity.moment;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;

/**
 * (Moment)表实体类
 *
 * @author makejava
 * @since 2023-05-21 23:22:11
 */
@SuppressWarnings("serial")
@TableName("bs_moment")
@Data
public class Moment extends Model<Moment> {
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
    //点赞数
    private int supportCount;
    //反对数
    private int opposeCount;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE, updateStrategy = FieldStrategy.NOT_EMPTY)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    //删除标志
    private Integer deleted;

}

