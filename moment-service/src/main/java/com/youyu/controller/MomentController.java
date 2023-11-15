package com.youyu.controller;

import com.youyu.dto.moment.MomentListInput;
import com.youyu.dto.moment.MomentListOutput;
import com.youyu.dto.common.PageOutput;
import com.youyu.entity.moment.Moment;
import com.youyu.entity.moment.MomentUserOutput;
import com.youyu.result.ResponseResult;
import com.youyu.service.MomentService;
import com.youyu.utils.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * (Moment)表控制层
 *
 * @author makejava
 * @since 2023-05-21 23:22:07
 */
@RestController
@RequestMapping("/moment")
public class MomentController {

    @Resource
    private MomentService momentService;

    @RequestMapping("/create")
    public ResponseResult<MomentListOutput> create(@Valid Moment input) {
        input.setUserId(SecurityUtils.getUserId());
        MomentListOutput output = momentService.create(input);
        return ResponseResult.success(output);
    }

    @RequestMapping("/update")
    public ResponseResult<MomentListOutput> update(@Valid Moment input) {
        input.setUpdateTime(new Date());
        momentService.updateById(input);
        MomentListOutput moment = momentService.getMoment(input.getId());
        return ResponseResult.success(moment);
    }

    @RequestMapping("/delete")
    public ResponseResult<Boolean> create(@RequestParam Long momentId) {
        boolean remove = momentService.delete(momentId);
        return ResponseResult.success(remove);
    }

    @RequestMapping("/open/list")
    public ResponseResult<PageOutput<MomentListOutput>> list(MomentListInput input) {
        PageOutput<MomentListOutput> momentList = momentService.momentList(input);
        return ResponseResult.success(momentList);
    }

    @RequestMapping("/open/list/following")
    public ResponseResult<PageOutput<MomentListOutput>> listFollowing(MomentListInput input) {
        PageOutput<MomentListOutput> momentList = momentService.momentListFollow(input);
        return ResponseResult.success(momentList);
    }

    @RequestMapping("/open/get")
    public ResponseResult<MomentListOutput> getMomentDetail(@RequestParam Long momentId) {
        return ResponseResult.success(momentService.getMoment(momentId));
    }

    @RequestMapping("/open/getMomentUserDetailById")
    public ResponseResult<MomentUserOutput> getMomentUserDetailById(Long userId) {
        MomentUserOutput userDetailById = momentService.getMomentUserDetailById(userId, true);
        return ResponseResult.success(userDetailById);
    }

    @RequestMapping("/open/selectById")
    public ResponseResult<Moment> selectById(Long momentId) {
        return ResponseResult.success(momentService.getById(momentId));
    }

    @RequestMapping("/open/momentListByIds")
    ResponseResult<List<MomentListOutput>> momentListByIds(@RequestParam List<Long> momentIds) {
        List<MomentListOutput> momentListOutputs = momentService.momentListByIds(momentIds);
        return ResponseResult.success(momentListOutputs);
    }
}

