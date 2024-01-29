package com.youyu.entity.moment;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * (MomentComment)表实体类
 *
 * @author makejava
 * @since 2023-06-18 20:28:05
 */
@Data
@TableName("bs_moment_comment")
@SuppressWarnings("serial")
public class MomentComment extends Model<MomentComment> {
    //主键
    private Long id;
    //回复的时刻id
    @NotNull(message = "时刻id不能为空")
    private Long momentId;
    //根评论id
    private Long rootId;
    //回复人id
    @NotNull(message = "回复人id不能为空")
    private Long userId;
    //被回复人id
    @NotNull(message = "被回复人id不能为空")
    private Long userIdTo;
    //回复了哪条子评论
    private Long replyId;
    //回复内容
    @NotBlank(message = "内容不能为空")
    private String content;
    private String images;
    //区域编号
    private Integer adcode;
    //区域名称
    @TableField(exist = false)
    private String adname;
    //支持数量
    private Long supportCount;
    //反对数量
    private Long opposeCount;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE, updateStrategy = FieldStrategy.NOT_EMPTY)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    //删除标志
    private Integer deleted;
}

