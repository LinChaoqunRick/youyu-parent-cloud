package com.youyu.dto.moment;

import com.youyu.dto.common.PageBase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MomentCommentListInput extends PageBase {
    Long momentId;
    Long rootId;
    String orderBy = "create_time";
}
