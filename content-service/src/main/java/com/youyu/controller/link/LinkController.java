package com.youyu.controller.link;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.annotation.Log;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.link.LinkListInput;
import com.youyu.entity.link.Link;
import com.youyu.entity.user.Message;
import com.youyu.enums.LogType;
import com.youyu.result.ResponseResult;
import com.youyu.service.link.LinkService;
import com.youyu.utils.PageUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * (Link：友链)表控制层
 *
 * @author makejava
 * @since 2025-09-11 15:26:54
 */
@RestController
@RequestMapping("link")
public class LinkController {

    @Resource
    private LinkService linkService;

    @RequestMapping("create")
    @Log(title = "新增友链", type = LogType.INSERT)
    ResponseResult<Boolean> createLink(@Valid Link link) {
        boolean save = linkService.save(link);
        return ResponseResult.success(save);
    }

    @RequestMapping("update")
    @Log(title = "更新友链", type = LogType.INSERT)
    ResponseResult<Boolean> updateLink(@Valid Link link) {
        boolean update = linkService.updateById(link);
        return ResponseResult.success(update);
    }

    @RequestMapping("/showHide")
    @Log(title = "显隐友链", type = LogType.UPDATE)
    public ResponseResult<Boolean> showHide(@RequestParam("ids") String ids, @RequestParam("status") Integer status) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .filter(StringUtils::hasText) // 过滤空值
                .map(Long::valueOf)              // 转换成 Long
                .collect(Collectors.toList());

        boolean update = linkService.update(
                new LambdaUpdateWrapper<Link>()
                        .in(Link::getId, idList)
                        .set(Link::getStatus, status)
        );

        return ResponseResult.success(update);
    }

    @RequestMapping("delete")
    @Log(title = "删除友链", type = LogType.INSERT)
    ResponseResult<Boolean> deleteLink(@RequestParam("ids") String ids) {
        boolean remove = linkService.removeByIds(Arrays.stream(ids.split(",")).toList());
        return ResponseResult.success(remove);
    }

    @RequestMapping("page")
    ResponseResult<PageOutput<Link>> pageLink(@Valid LinkListInput input) {
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(input.getName()), Link::getName, input.getName());
        queryWrapper.orderByDesc(Link::getCreateTime);
        Page<Link> page = new Page<>(input.getPageNum(), input.getPageSize());

        Page<Link> linkPage = linkService.page(page, queryWrapper);
        PageOutput<Link> pageOutput = PageUtils.setPageResult(linkPage, Link.class);

        return ResponseResult.success(pageOutput);
    }
}

