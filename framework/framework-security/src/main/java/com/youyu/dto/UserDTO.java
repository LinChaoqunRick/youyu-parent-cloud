package com.youyu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class UserDTO implements Serializable {
    private Long id;
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
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private int deleted;
}
