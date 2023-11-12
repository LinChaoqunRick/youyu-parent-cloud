package com.youyu.dto.moment;

import com.youyu.dto.common.PageBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MomentLikeUserListInput extends PageBase {
    private Long momentId;
    public int pageSize = 3;

    public MomentLikeUserListInput(Long momentId) {
        this.momentId = momentId;
    }
}
