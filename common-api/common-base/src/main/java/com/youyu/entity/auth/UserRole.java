package com.youyu.entity.auth;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * (SysUserRole)实体类
 *
 * @author makejava
 * @since 2023-04-24 21:00:45
 */
@AllArgsConstructor
@Data
@TableName("sys_user_role")
public class UserRole implements Serializable {
    private static final long serialVersionUID = -85667093032311908L;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 角色id
     */
    private Long roleId;


}

