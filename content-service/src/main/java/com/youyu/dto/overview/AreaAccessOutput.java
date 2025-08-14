package com.youyu.dto.overview;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AreaAccessOutput {
    private String areaCode;
    private String areaName;
    private Long count;
}
