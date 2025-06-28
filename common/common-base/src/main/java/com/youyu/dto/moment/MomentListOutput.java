package com.youyu.dto.moment;

import com.youyu.entity.moment.Moment;
import com.youyu.entity.moment.MomentUserOutput;
import lombok.Data;

import java.util.List;

@Data
public class MomentListOutput extends Moment {
    private MomentUserOutput user;
    private int commentCount = 0;
    private boolean momentLike;
    private List<MomentUserOutput> likeUsers;
}
