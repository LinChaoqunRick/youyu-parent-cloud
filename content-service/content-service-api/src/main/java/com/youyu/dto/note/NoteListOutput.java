package com.youyu.dto.note;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class NoteListOutput {
    private Long id;
    private String name;
    private Long userId;
    private String introduce;
    private String cover;
    private String type;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private Long viewCount = 0L;
    private Long subscribeCount = 0L;
    private Integer chapterCount = 0;
    private NoteUserOutput user;
}
