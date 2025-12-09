package com.youyu.controller.moment;


import com.youyu.annotation.Log;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.moment.MomentCommentListInput;
import com.youyu.dto.moment.MomentCommentListOutput;
import com.youyu.entity.user.Visitor;
import com.youyu.entity.moment.MomentComment;
import com.youyu.entity.result.TencentLocationResult;
import com.youyu.enums.LogType;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.feign.UserServiceClient;
import com.youyu.result.ResponseResult;
import com.youyu.service.moment.MomentCommentService;
import com.youyu.utils.LocateUtils;
import com.youyu.utils.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * (MomentComment)表控制层
 *
 * @author makejava
 * @since 2023-06-18 20:28:01
 */
@RestController
@RequestMapping("/momentComment")
public class MomentCommentController {
    /**
     * 服务对象
     */
    @Resource
    private MomentCommentService momentCommentService;

    @Resource
    private UserServiceClient userServiceClient;

    @Resource
    private LocateUtils locateUtils;

    @RequestMapping("/open/create")
    @Log(title = "新增时刻评论", type = LogType.INSERT)
    @Transactional
    public ResponseResult<MomentCommentListOutput> create(@Valid MomentComment input) {
        if (SecurityUtils.getUserId() == null && input.getEmail() == null) {
            throw new SystemException(ResultCode.PARAMETER_ERROR.getCode(), "操作者不能为空");
        }
        if (SecurityUtils.getUserId() != null) {
            // 用户登录
            input.setUserId(SecurityUtils.getUserId());
        } else {
            // 游客
            Visitor visitor = new Visitor();
            visitor.setEmail(input.getEmail());
            visitor.setNickname(input.getNickname());
            visitor.setHomepage(input.getHomepage());
            visitor = userServiceClient.saveOrUpdateByEmail(visitor).getData();
            input.setVisitorId(visitor.getId());
        }
        TencentLocationResult locationResult = locateUtils.queryTencentIp();
        input.setAdcode(locationResult.getAdcode());
        MomentCommentListOutput detail = momentCommentService.createComment(input);
        detail.setAdname(LocateUtils.getShortNameByCode(String.valueOf(locationResult.getAdcode())));
        return ResponseResult.success(detail);
    }

    @RequestMapping("/delete")
    @Log(title = "删除时刻评论", type = LogType.DELETE)
    public ResponseResult<Boolean> delete(@RequestParam Long commentId) {
        boolean remove = momentCommentService.deleteComment(commentId);
        return ResponseResult.success(remove);
    }

    @RequestMapping("/getById")
    public ResponseResult<MomentComment> getById(Long commentId) {
        MomentComment comment = momentCommentService.getById(commentId);
        return ResponseResult.success(comment);
    }

    @RequestMapping("/open/momentCommentPage")
    public ResponseResult<PageOutput<MomentCommentListOutput>> momentCommentPage(@Valid MomentCommentListInput input) {
        PageOutput<MomentCommentListOutput> comments = momentCommentService.momentCommentPage(input);
        return ResponseResult.success(comments);
    }
}

