package com.youyu.controller.moment;


import com.youyu.dto.common.PageOutput;
import com.youyu.dto.moment.MomentCommentListInput;
import com.youyu.dto.moment.MomentCommentListOutput;
import com.youyu.dto.moment.MomentReplyListInput;
import com.youyu.entity.moment.MomentComment;
import com.youyu.entity.user.PositionInfo;
import com.youyu.feign.UserServiceClient;
import com.youyu.result.ResponseResult;
import com.youyu.service.moment.MomentCommentService;
import com.youyu.utils.SecurityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;

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

    @RequestMapping("/create")
    public ResponseResult<MomentCommentListOutput> create(@Valid MomentComment input) {
        input.setUserId(SecurityUtils.getUserId());
        PositionInfo positionInfo = userServiceClient.ipLocation().getData();
        input.setAdcode(positionInfo.getAdcode());
        MomentCommentListOutput detail = momentCommentService.createComment(input);
        detail.setAdname(positionInfo.getAdname());
        return ResponseResult.success(detail);
    }

    @RequestMapping("/delete")
    public ResponseResult<Boolean> delete(Long commentId) {
        boolean remove = momentCommentService.deleteComment(commentId);
        return ResponseResult.success(remove);
    }

    @RequestMapping("/getById")
    public ResponseResult<MomentComment> getById(Long commentId) {
        MomentComment comment = momentCommentService.getById(commentId);
        return ResponseResult.success(comment);
    }

    @RequestMapping("/open/listMomentCommentPage")
    public ResponseResult<PageOutput<MomentCommentListOutput>> listMomentCommentPage(@Valid MomentCommentListInput input) {
        PageOutput<MomentCommentListOutput> comments = momentCommentService.listMomentCommentPage(input);
        return ResponseResult.success(comments);
    }

    @RequestMapping("/open/listMomentCommentAll")
    public ResponseResult<List<MomentCommentListOutput>> listMomentCommentAll(@Valid MomentCommentListInput input) {
        List<MomentCommentListOutput> outputList = momentCommentService.listMomentCommentAll(input);
        return ResponseResult.success(outputList);
    }

    @RequestMapping("/open/listMomentReplyPage")
    public ResponseResult<PageOutput<MomentCommentListOutput>> listMomentReplyPage(@Valid MomentReplyListInput input) {
        PageOutput<MomentCommentListOutput> comments = momentCommentService.listMomentReplyPage(input);
        return ResponseResult.success(comments);
    }
}

