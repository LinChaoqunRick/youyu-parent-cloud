package com.youyu.entity.moment;

import com.youyu.entity.user.Actor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MomentUserOutput extends Actor {
    private MomentUserExtraInfo extraInfo;
}
