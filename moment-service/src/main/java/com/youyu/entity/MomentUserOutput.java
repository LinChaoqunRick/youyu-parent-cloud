package com.youyu.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class MomentUserOutput {
    private Long id;
    private String nickname;
    private String avatar;
    private Integer sex;
    private Integer level;
    private String signature;
    private String status;
    private MomentUserExtraInfo extraInfo;
    private boolean follow;
}
