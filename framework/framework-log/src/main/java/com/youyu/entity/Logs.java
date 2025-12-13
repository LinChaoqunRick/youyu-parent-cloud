package com.youyu.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("bs_logs")
public class Logs {
    @TableId
    private Long id;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 客户端Id
     */
    private String clientId;
    /**
     * 操作名称
     */
    private String name;
    /**
     * 操作类型
     */
    private Integer type;
    /**
     * 访问ip
     */
    private String ip;
    /**
     * 访问路径
     */
    private String path;
    /**
     * 请求方式
     */
    private String method;
    /**
     * 访问ip所在区域
     */
    private Integer adcode;
    /**
     * 访问ip所在区域名称
     */
    @TableField(exist = false)
    private String adName;
    /**
     * 持续时间
     */
    private Long duration;
    /**
     * 请求参数
     */
    private String requestData;
    /**
     * 响应参数
     */
    private String responseData;
    /**
     * 结果0：成功，1：失败
     */
    private Integer result;
    /**
     * 错误信息
     */
    private String error;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, updateStrategy = FieldStrategy.NOT_EMPTY)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**
     * 删除标志
     */
    @JsonIgnore
    private Integer deleted;
}
