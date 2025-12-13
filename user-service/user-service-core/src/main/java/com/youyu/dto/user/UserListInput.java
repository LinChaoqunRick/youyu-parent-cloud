package com.youyu.dto.user;

import com.youyu.dto.page.PageBase;
import lombok.Data;

@Data
public class UserListInput extends PageBase {
    private String key;
}
