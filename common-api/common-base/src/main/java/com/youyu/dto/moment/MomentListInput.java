package com.youyu.dto.moment;

import com.youyu.dto.common.PageBase;
import lombok.Data;

@Data
public class MomentListInput extends PageBase {
    private String userIds;
    private String orderBy = "create_time";
}
