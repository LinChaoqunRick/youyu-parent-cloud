package com.youyu.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOutput {
    private Long id;

    private String nickname;

    private String avatar;

    private Integer sex;

    private String email;

    private Integer level;

    private Date registerDate;

    private String signature;
}
