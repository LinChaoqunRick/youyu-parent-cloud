package com.youyu.entity.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("bs_user")
public class UserFramework implements Serializable {
    private static final long serialVersionUID = -40356785423868312L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @NotBlank(message = "用户名不能为空")
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private Integer sex;
    private String email;
    private Integer adcode;
    private String adname;
    private String homepage;
    private String githubId;
    private String qqId;
    private Integer level;
    private Date registerDate;
    private Date birthday;
    private String signature;
    private int status;
//    private int deleted;
}
