package com.youyu.entity.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.io.Serializable;

/**
 * (User)实体类
 *
 * @author makejava
 * @since 2023-02-10 21:05:47
 */
@EqualsAndHashCode(callSuper = true)
@TableName("bs_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends Model<User> implements Serializable {
    private static final long serialVersionUID = -81729684263381566L;

    @NotNull(message = "用户id不能为空")
    private Long id;
    /**
     * 用户名、手机号码
     */
    private String username;
    /**
     * 密码
     */
    @TableField(select = false)
    private String password;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像e
     */
    private String avatar;
    /**
     * 性别
     */
    private Integer sex;
    /**
     * 邮箱地址
     */
    private String email;
    /**
     * 地址编号
     */
    private Integer adcode;
    /**
     * 地址简称
     */
    @TableField(exist = false)
    private String adname;
    /**
     * 个人主页
     */
    private String homepage;
    /**
     * github用户Id
     */
    private String githubId;
    /**
     * qq用户Id
     */
    private String qqId;
    /**
     * 等级
     */
    private Integer level;
    /**
     * 注册日期
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date registerDate;
    /**
     * 生日
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    /**
     * 个性签名
     */
    private String signature;
    /**
     * 启用状态
     */
    private String status;

    private Integer deleted;
}

