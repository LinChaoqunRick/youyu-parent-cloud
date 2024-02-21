package com.youyu.entity.user;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * (Message)表实体类
 *
 * @author makejava
 * @since 2024-02-20 22:04:01
 */
@SuppressWarnings("serial")
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

    @NotBlank(message = "内容不能为空")
    private String content;
    //省份编号
    private Integer adcode;

    private Long supportCount;

    private Long opposeCount;

    private Date createTime;

    private Date updateTime;
    //删除标志
    private Integer deleted;
}

