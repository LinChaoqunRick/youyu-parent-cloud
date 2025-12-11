package com.youyu.dto.moment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MomentListOutput {
    private Long id;
    private Long userId;
    private String content;
    private Integer mood;
    private Integer topicId;
    private String images;
    private String longitude;
    private String latitude;
    private String location;
    private Integer adcode;
    private String adname;
    private Long supportCount;
    private Long opposeCount;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private MomentUserOutput user;
    private int commentCount = 0;
    private boolean momentLike;
    private List<MomentUserOutput> likeUsers;
}
