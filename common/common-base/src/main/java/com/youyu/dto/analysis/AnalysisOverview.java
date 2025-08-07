package com.youyu.dto.analysis;

import lombok.Data;

@Data
public class AnalysisOverview {
    private Long userNumber;
    private Long postNumber;
    private Long momentNumber;
    private Long noteNumber;
    private Long chapterNumber;
    private Long albumNumber;
    private Long messageNumber;
    private Long todayVisitNumber;
    private Long monthVisitNumber;
}
