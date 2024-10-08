package com.youyu.entity.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class UserDetailOutput {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private Integer sex;
    private String email;
    private String homepage;
    private String githubId;
    private String qqId;
    private Integer level;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date registerDate;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
    private String signature;
    private String status;
}
