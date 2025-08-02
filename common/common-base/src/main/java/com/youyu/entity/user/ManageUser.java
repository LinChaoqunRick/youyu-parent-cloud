package com.youyu.entity.user;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Getter;
import lombok.Setter;

/**
 * (ManageUser)表实体类
 *
 * @author makejava
 * @since 2025-08-02 09:21:54
 */
@Setter
@Getter
@SuppressWarnings("serial")
@TableName("manage_user")
public class ManageUser extends Model<ManageUser> {

    @TableId
    private Long id;
    //用户名、手机号码
    private String username;
    //密码
    private String password;
    //昵称
    private String nickname;
    //头像
    private String avatar;
    //性别
    private Integer sex;
    //邮箱地址
    private String email;
    //省份编号
    private Integer adcode;
    //个性签名
    private String signature;
    //启用状态
    private String status;

    private Date createTime;

    private Date updateTime;

    private Integer deleted;
}

