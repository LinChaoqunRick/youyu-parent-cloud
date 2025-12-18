package com.youyu.dto.moment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youyu.dto.Actor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MomentCommentOutput {
    // 主键
    private Long id;
    private Long momentId;
    private Long rootId;
    private Long userId;
    private Long visitorId;
    private Long replyId;
    private String content;
    private String images;
    private Integer adcode;
    private String adname;
    private Long supportCount;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    // 以下新增
    private Long replyCount = 0L;
    private Actor actor;
    private Actor actorTo;
    private boolean commentLike;
    private List<MomentCommentOutput> children;
}
