package com.youyu.controller.moment;

import com.youyu.annotation.Log;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.moment.MomentListInput;
import com.youyu.dto.moment.MomentListOutput;
import com.youyu.entity.moment.Moment;
import com.youyu.entity.moment.MomentUserOutput;
import com.youyu.entity.result.TencentLocationResult;
import com.youyu.enums.LogType;
import com.youyu.feign.UserServiceClient;
import com.youyu.result.ResponseResult;
import com.youyu.service.moment.MomentService;
import com.youyu.utils.LocateUtils;
import com.youyu.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * (Moment)表控制层
 *
 * @author makejava
 * @since 2023-05-21 23:22:07
 */
@RestController
@Slf4j
@RequestMapping("/moment")
@Validated
public class MomentController {

    @Resource
    private MomentService momentService;

    @Resource
    private UserServiceClient userServiceClient;

    @Resource
    private LocateUtils locateUtils;

    @RequestMapping("/create")
    @Log(title = "新增时刻", type = LogType.INSERT)
    public ResponseResult<MomentListOutput> create(@Validated @RequestBody Moment input) {
        input.setUserId(SecurityUtils.getUserId());
        TencentLocationResult locationResult = locateUtils.queryTencentIp();
        input.setAdcode(locationResult.getAdcode());
        MomentListOutput output = momentService.create(input);
        output.setAdname(LocateUtils.getShortNameByCode(String.valueOf(locationResult.getAdcode())));
        return ResponseResult.success(output);
    }

    @RequestMapping("/update")
    @Log(title = "更新时刻", type = LogType.UPDATE)
    public ResponseResult<MomentListOutput> update(@Validated @RequestBody Moment input) {
        input.setUpdateTime(new Date());
        momentService.updateById(input);
        MomentListOutput moment = momentService.getMoment(input.getId());
        return ResponseResult.success(moment);
    }

    @RequestMapping("/delete")
    @Log(title = "删除时刻", type = LogType.DELETE)
    public ResponseResult<Boolean> delete(@RequestParam Long momentId) {
        boolean remove = momentService.delete(momentId);
        return ResponseResult.success(remove);
    }

    @RequestMapping("/open/list")
    public ResponseResult<PageOutput<MomentListOutput>> list(MomentListInput input) {
        PageOutput<MomentListOutput> momentList = momentService.getMomentList(input);
        return ResponseResult.success(momentList);
    }

    @RequestMapping("/list/following")
    public ResponseResult<PageOutput<MomentListOutput>> listFollowing(MomentListInput input) {
        PageOutput<MomentListOutput> momentList = momentService.getMomentListFollow(input);
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

