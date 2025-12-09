package com.youyu.controller.post;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youyu.annotation.Log;
import com.youyu.dto.post.column.ColumnListOutput;
import com.youyu.dto.post.column.ColumnPostInput;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.post.post.PostListOutput;
import com.youyu.entity.user.Column;
import com.youyu.enums.LogType;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.result.ResponseResult;

import com.youyu.service.post.ColumnService;
import com.youyu.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
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

    @Value("${column.columnMaxNum}")
    private Long columnMaxNum;

    @RequestMapping("/open/list")
    public ResponseResult<List<ColumnListOutput>> getColumnList(@RequestParam Long userId, Integer count) {
        List<ColumnListOutput> columnList = columnService.getColumnList(userId, count);
        return ResponseResult.success(columnList);
    }

    @RequestMapping("/open/get")
    public ResponseResult<ColumnListOutput> getColumnDetail(@RequestParam Long columnId) {
        ColumnListOutput columnDetail = columnService.getColumnDetail(columnId);
        return ResponseResult.success(columnDetail);
    }

    @RequestMapping("/create")
    @Log(title = "新增专栏", type = LogType.INSERT)
    public ResponseResult<Column> createColumn(@Valid Column column) {
        LambdaQueryWrapper<Column> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Column::getUserId, SecurityUtils.getUserId());
        long count = columnService.count(queryWrapper);
        if (count >= columnMaxNum) {
            throw new SystemException(ResultCode.OTHER_ERROR.getCode(), "专栏数目已达上限" + columnMaxNum + "个");
        }
        column.setUserId(SecurityUtils.getUserId());
        columnService.save(column);
        return ResponseResult.success(column);
    }

    @RequestMapping("/update")
    @Log(title = "更新专栏", type = LogType.UPDATE)
    public ResponseResult<Column> updateColumn(@Valid Column column) {
        column.setUpdateTime(new Date());
        columnService.updateById(column);
        return ResponseResult.success(column);
    }

    @RequestMapping("/delete")
    @Log(title = "删除专栏", type = LogType.DELETE)
    public ResponseResult<Boolean> deleteColumn(@RequestParam Long columnId) {
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

