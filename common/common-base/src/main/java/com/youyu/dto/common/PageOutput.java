package com.youyu.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageOutput<T> {
    private List<T> list = new ArrayList<>();
    private Long current = 1L;
    private Long pages = 1L;
    private Long size = 10L;
    private Long total = 0L;

}
