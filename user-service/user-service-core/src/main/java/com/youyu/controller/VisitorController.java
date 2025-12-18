package com.youyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.dto.RegionData;
import com.youyu.dto.VisitData;
import com.youyu.dto.page.PageOutput;
import com.youyu.dto.user.VisitorPageInput;
import com.youyu.entity.user.Visitor;
import com.youyu.result.ResponseResult;
import com.youyu.service.VisitorService;
import com.youyu.utils.AvatarUtils;
import com.youyu.utils.PageUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/visitor")
public class VisitorController {
    @Resource
    private VisitorService visitorService;

    /**
     * 根据email获取游客信息
     * 如果是新游客（数据库中不存在），则根据邮箱生成头像URL并返回
     * @param email 邮箱
     * @return 游客信息
     */
    @RequestMapping("/open/getVisitorByEmail")
    public ResponseResult<Visitor> getVisitorByEmail(@RequestParam String email) {
        Visitor visitor = visitorService.getVisitorByEmail(email);

        // 如果是新游客，生成头像信息
        if (visitor == null) {
            visitor = new Visitor();
            visitor.setEmail(email);
            visitor.setAvatar(AvatarUtils.generateAvatarUrl(email));
        }

        return ResponseResult.success(visitor);
    }

    /**
     * 保存或更新游客信息
     * @param visitor 游客
     * @return 更新后的游客信息
     */
    @RequestMapping("/open/saveOrUpdateByEmail")
    public ResponseResult<Visitor> saveOrUpdateByEmail(@RequestBody Visitor visitor) {
        visitorService.saveOrUpdateByEmail(visitor);
        return ResponseResult.success(visitor);
    }

    /**
     * 根据id列表获取游客信息
     * @param ids id列表
     * @return 游客信息
     */
    @RequestMapping("/open/selectBatchIds")
    public ResponseResult<List<Visitor>> selectBatchIds(@RequestBody List<Long> ids) {
        List<Visitor> visitors = visitorService.listByIds(ids);
        return ResponseResult.success(visitors);
    }

    /**
     * 获取游客总数
     * @return 游客总数
     */
    @RequestMapping("/getVisitorTotal")
    public ResponseResult<Long> getVisitorTotal() {
        Long total = visitorService.getVisitorTotal();
        return ResponseResult.success(total);
    }

    /**
     * 按月统计新增游客趋势（过去12个月）
     * @return 每月新增游客数据
     */
    @RequestMapping("/getMonthlyNewVisitors")
    public ResponseResult<List<VisitData>> getMonthlyNewVisitors() {
        List<VisitData> result = visitorService.getMonthlyNewVisitors();
        return ResponseResult.success(result);
    }

    /**
     * 按省份统计游客地域分布
     * @return 各省份游客数据
     */
    @RequestMapping("/getVisitorsByProvince")
    public ResponseResult<List<RegionData>> getVisitorsByProvince() {
        List<RegionData> result = visitorService.getVisitorsByProvince();
        return ResponseResult.success(result);
    }

    /**
     * 分页查询游客列表
     *
     * @param input 分页查询参数
     * @return 分页结果
     */
    @RequestMapping("/page")
    public ResponseResult<PageOutput<Visitor>> page(VisitorPageInput input) {
        LambdaQueryWrapper<Visitor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(input.getNickname() != null && !input.getNickname().isEmpty(), Visitor::getNickname, input.getNickname());
        queryWrapper.like(input.getEmail() != null && !input.getEmail().isEmpty(), Visitor::getEmail, input.getEmail());
        queryWrapper.orderByDesc(Visitor::getId);

        Page<Visitor> page = visitorService.page(new Page<>(input.getPageNum(), input.getPageSize()), queryWrapper);
        PageOutput<Visitor> output = PageUtils.setPageResult(page, Visitor.class);
        return ResponseResult.success(output);
    }
}
