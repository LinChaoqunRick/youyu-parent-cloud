package com.youyu.controller;


import com.youyu.dto.MomentCommentListInput;
import com.youyu.dto.MomentCommentListOutput;
import com.youyu.dto.MomentReplyListInput;
import com.youyu.dto.common.PageOutput;
import com.youyu.entity.MomentComment;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.result.ResponseResult;
import com.youyu.service.MomentCommentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
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

    @RequestMapping("/create")
    public ResponseResult<MomentCommentListOutput> create(@Valid MomentComment input) {
        MomentCommentListOutput detail = momentCommentService.createComment(input);
        return ResponseResult.success(detail);
    }

    @RequestMapping("/listMomentCommentPage")
    public ResponseResult<PageOutput<MomentCommentListOutput>> listMomentCommentPage(@Valid MomentCommentListInput input) {
        PageOutput<MomentCommentListOutput> comments = momentCommentService.listMomentCommentPage(input);
        return ResponseResult.success(comments);
    }

    @RequestMapping("/listMomentCommentAll")
    public ResponseResult<List<MomentCommentListOutput>> listMomentCommentAll(@Valid MomentCommentListInput input) {
        List<MomentCommentListOutput> outputList = momentCommentService.listMomentCommentAll(input);
        return ResponseResult.success(outputList);
    }

    @RequestMapping("/delete")
    public ResponseResult<Boolean> delete(Long commentId) {
        boolean remove = momentCommentService.deleteComment(commentId);
        return ResponseResult.success(remove);
    }

    @RequestMapping("/listMomentReplyPage")
    public ResponseResult<PageOutput<MomentCommentListOutput>> listMomentReplyPage(@Valid MomentReplyListInput input) {
        PageOutput<MomentCommentListOutput> comments = momentCommentService.listMomentReplyPage(input);
        return ResponseResult.success(comments);
    }
}

