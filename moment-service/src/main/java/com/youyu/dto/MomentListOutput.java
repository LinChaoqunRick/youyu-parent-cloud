package com.youyu.dto;

import com.youyu.entity.Moment;
import com.youyu.entity.MomentUserOutput;
import lombok.Data;

import java.util.List;

@Data
public class MomentListOutput extends Moment {
    private MomentUserOutput user;
    private int commentCount = 0;
    private boolean momentLike;
    private List<MomentUserOutput> likeUsers;
}
