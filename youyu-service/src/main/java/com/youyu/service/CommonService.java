package com.youyu.service;

import com.github.pagehelper.PageInfo;
import com.youyu.dto.DynamicListInput;

public interface CommonService {
    PageInfo<Object> getUserDynamics(DynamicListInput input);
}
