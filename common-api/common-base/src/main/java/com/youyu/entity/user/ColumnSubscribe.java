package com.youyu.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;
import java.io.Serializable;

/**
 * (ColumnSubscribe)实体类
 *
 * @author makejava
 * @since 2023-03-23 21:30:39
 */
@TableName("bs_column_subscribe")
public class ColumnSubscribe implements Serializable {
    private static final long serialVersionUID = 864287423394943807L;
    /**
     * 主键
     */
    private Long id;
    /**
     * 订阅人
     */
    private Long userId;
    /**
     * 专栏id
     */
    private Long columnId;
    /**
     * 订阅时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}

