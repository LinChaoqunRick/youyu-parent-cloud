package com.youyu.entity.auth;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("bs_logs")
public class ActionLog {
    @TableId
    private Long id;
    private Long userId;
    private String type;
    private String ip;
    private String action;
    private String path;
    private Long duration;
    private String params;
    private Integer result;
    private Integer deleted;
}
