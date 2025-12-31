package com.youyu.controller.moment;


import com.youyu.annotation.Log;
import com.youyu.common.VisitorCommentHelper;
import com.youyu.dto.page.PageOutput;
import com.youyu.dto.moment.MomentCommentListInput;
import com.youyu.dto.moment.MomentCommentOutput;
import com.youyu.entity.moment.MomentComment;
import com.youyu.dto.result.TencentLocationResult;
import com.youyu.enums.LogType;
import com.youyu.result.ResponseResult;
import com.youyu.service.moment.MomentCommentService;
import com.youyu.utils.LocateUtils;
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
    private VisitorCommentHelper visitorCommentHelper;

    @RequestMapping("/open/create")
    @Log(title = "新增时刻评论", type = LogType.INSERT)
    @Transactional
    public ResponseResult<MomentCommentOutput> create(@Valid MomentComment input) {
        TencentLocationResult locationResult = visitorCommentHelper.processCommentIdentity(input);
        MomentCommentOutput detail = momentCommentService.createComment(input);
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
    public ResponseResult<PageOutput<MomentCommentOutput>> momentCommentPage(@Valid MomentCommentListInput input) {
        PageOutput<MomentCommentOutput> comments = momentCommentService.momentCommentPage(input);
        return ResponseResult.success(comments);
    }
}

