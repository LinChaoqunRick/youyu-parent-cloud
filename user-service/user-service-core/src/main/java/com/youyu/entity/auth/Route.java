package com.youyu.entity.auth;

import java.io.Serializable;

/**
 * (Route)实体类
 *
 * @author makejava
 * @since 2023-01-04 19:41:20
 */
public class Route implements Serializable {
    private static final long serialVersionUID = 526044276510510351L;

    private Integer id;
    /**
     * 路由名称
     */
    private String title;
    /**
     * 路由编码
     */
    private String code;
    /**
     * 父id
     */
    private Integer pid;
    /**
     * 路由描述
     */
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

