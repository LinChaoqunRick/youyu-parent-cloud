package com.youyu.controller;

import com.youyu.entity.user.Visitor;
import com.youyu.result.ResponseResult;
import com.youyu.service.VisitorService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/visitor/open")
public class VisitorController {
    @Resource
    private VisitorService visitorService;

    /**
     * 根据email获取游客信息
     * @param email 邮箱
     * @return 游客
     */
    @RequestMapping("/getVisitorByEmail")
    public ResponseResult<Visitor> getVisitorByEmail(@RequestParam String email) {
        Visitor visitor = visitorService.getVisitorByEmail(email);
        return ResponseResult.success(visitor);
    }

    /**
     * 保存或更新游客信息
     * @param visitor 游客
     * @return 更新后的游客信息
     */
    @RequestMapping("/saveOrUpdateByEmail")
    public ResponseResult<Visitor> saveOrUpdateByEmail(@RequestBody Visitor visitor) {
        visitorService.saveOrUpdateByEmail(visitor);
        return ResponseResult.success(visitor);
    }

    /**
     * 根据id列表获取游客信息
     * @param ids id列表
     * @return 游客信息
     */
    @RequestMapping("/selectBatchIds")
    public ResponseResult<List<Visitor>> selectBatchIds(@RequestBody List<Long> ids) {
        List<Visitor> visitors = visitorService.listByIds(ids);
        return ResponseResult.success(visitors);
    }
}
