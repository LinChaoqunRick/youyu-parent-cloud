package com.youyu.controller.post;

import com.youyu.annotation.Log;
import com.youyu.dto.post.comment.CommentListInput;
import com.youyu.dto.post.comment.CommentListOutput;
import com.youyu.dto.post.comment.PostReplyListInput;
import com.youyu.dto.common.PageOutput;
import com.youyu.entity.post.Comment;
import com.youyu.entity.post.CommentLike;
import com.youyu.enums.LogType;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.result.ResponseResult;
import com.youyu.service.post.CommentLikeService;
import com.youyu.service.post.CommentService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * (Comment)表控制层
 *
 * @author makejava
 * @since 2023-02-12 21:20:00
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    @Resource
    private CommentLikeService commentLikeService;

    @RequestMapping("/open/getCommentsPage")
    ResponseResult<PageOutput<CommentListOutput>> getCommentsPage(@Valid CommentListInput input) {
        PageOutput<CommentListOutput> output = commentService.getCommentsPage(input);
        return ResponseResult.success(output);
    }

    @RequestMapping("/open/getPostSubCommentsPage")
    ResponseResult<PageOutput<CommentListOutput>> getPostSubCommentsPage(PostReplyListInput input) {
        PageOutput<CommentListOutput> commentsPage = commentService.getPostSubCommentsPage(input);
        return ResponseResult.success(commentsPage);
    }

    @RequestMapping("/createComment")
    @Log(title = "新增文章评论", type = LogType.INSERT)
    ResponseResult<CommentListOutput> createPostComment(Comment comment) {
        CommentListOutput output = commentService.createPostComment(comment);
        return ResponseResult.success(output);
    }

    @RequestMapping("/deleteComment")
    @Log(title = "删除文章评论", type = LogType.DELETE)
    ResponseResult<Boolean> deleteComment(Long commentId) {
        boolean status = commentService.deleteComment(commentId);
        return ResponseResult.success(status);
    }

    @RequestMapping("/setLike")
    @Log(title = "点赞文章评论", type = LogType.INSERT)
    ResponseResult<CommentLike> setCommentLike(@Valid CommentLike commentLike) {
        boolean status = commentLikeService.setPostCommentLike(commentLike);
        if (status) {
            return ResponseResult.success(commentLike);
        } else {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
    }

    @Log(title = "取消点赞文章评论", type = LogType.DELETE)
    @RequestMapping("/cancelLike")
    ResponseResult<Boolean> cancelCommentLike(@Valid CommentLike commentLike) {
        boolean cancel = commentLikeService.cancelPostCommentLike(commentLike);
        if (cancel) {
            return ResponseResult.success(true);
        } else {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
    }

    @RequestMapping("/rectifySupportCount")
    @Log(title = "校正文章评论", type = LogType.OTHER)
    ResponseResult<String> rectifySupportCount() {
        commentLikeService.rectifySupportCount();
        return ResponseResult.success("校正完成");
    }
}

