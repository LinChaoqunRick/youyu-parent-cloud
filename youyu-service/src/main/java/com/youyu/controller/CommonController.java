package com.youyu.controller;

import com.github.pagehelper.PageInfo;
import com.youyu.dto.DynamicListInput;
import com.youyu.result.ResponseResult;
import com.youyu.service.CommonService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/common")
public class CommonController {
    @Resource
    private CommonService commonService;

    @RequestMapping("/getDynamics")
    ResponseResult<PageInfo<Object>> getDynamics(@Valid DynamicListInput input) {
        PageInfo<Object> pageInfo = commonService.getUserDynamics(input);
        return ResponseResult.success(pageInfo);
    }
}
