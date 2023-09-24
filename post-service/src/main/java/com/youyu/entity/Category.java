package com.youyu.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

/**
 * (BsCategory)表实体类
 *
 * @author makejava
 * @since 2023-01-01 22:31:17
 */
@SuppressWarnings("serial")
@TableName("bs_category")
public class Category extends Model<Category> {
    @TableId
    private Long id;
    //分类名
    private String name;
    //父Id
    private Long pid;
    //分类描述
    private String description;
    //状态：0正常，1:禁用
    private String status;
    //删除标志
    private Integer deleted;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}

