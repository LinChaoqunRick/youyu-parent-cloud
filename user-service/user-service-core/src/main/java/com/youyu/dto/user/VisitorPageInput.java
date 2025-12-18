package com.youyu.dto.user;

import com.youyu.dto.page.PageBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VisitorPageInput extends PageBase {
    private String nickname;
    private String email;
}
