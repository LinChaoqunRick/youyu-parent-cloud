package com.youyu.dto.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
public class PageBase {
    public int pageNum = 1;
    public int pageSize = 10;
    public String orderBy;
    public boolean asc = false; // 是否升序，true 为升序，false 为降序;
}
