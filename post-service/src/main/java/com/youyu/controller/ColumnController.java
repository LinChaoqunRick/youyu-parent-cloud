package com.youyu.controller;

import com.youyu.dto.post.column.ColumnListOutput;
import com.youyu.dto.post.column.ColumnPostInput;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.post.post.PostListOutput;
import com.youyu.entity.user.Column;
import com.youyu.result.ResponseResult;
import com.youyu.service.ColumnService;
import com.youyu.utils.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * (Column)表控制层
 *
 * @author makejava
 * @since 2023-03-13 22:02:29
 */
@RestController
@RequestMapping("/column")
public class ColumnController {
    /**
     * 服务对象
     */
    @Resource
    private ColumnService columnService;

    @RequestMapping("/open/list")
    public ResponseResult<List<ColumnListOutput>> getColumnList(Column column) {
        List<ColumnListOutput> columnList = columnService.getColumnList(column);
        return ResponseResult.success(columnList);
    }

    @RequestMapping("/open/get")
    public ResponseResult<ColumnListOutput> getColumnDetail(@RequestParam Long columnId) {
        ColumnListOutput columnDetail = columnService.getColumnDetail(columnId);
        return ResponseResult.success(columnDetail);
    }

    @RequestMapping("/add")
    public ResponseResult<Column> addColumn(@Valid Column column) {
        column.setUserId(SecurityUtils.getUserId());
        columnService.save(column);
        return ResponseResult.success(column);
    }

    @RequestMapping("/update")
    public ResponseResult<Column> updateColumn(@Valid Column column) {
        column.setUpdateTime(new Date());
        columnService.updateById(column);
        return ResponseResult.success(column);
    }

    @RequestMapping("/delete")
    public ResponseResult<Boolean> updateColumn(@RequestParam Long columnId) {
        columnService.removeById(columnId);
        return ResponseResult.success(true);
    }

    @RequestMapping("/open/getColumnPosts")
    public ResponseResult<PageOutput<PostListOutput>> getColumnPosts(ColumnPostInput getColumnPosts) {
        PageOutput<PostListOutput> columnPosts = columnService.getColumnPosts(getColumnPosts);
        return ResponseResult.success(columnPosts);
    }

    @RequestMapping("/setColumnIsTop")
    public ResponseResult<Boolean> setColumnIsTop(@RequestParam Long columnId, @RequestParam Boolean isTop) {
        int i = columnService.setColumnIsTop(columnId, isTop);
        return ResponseResult.success(i == 1);
    }
}

