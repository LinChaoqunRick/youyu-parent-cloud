package com.youyu.service.post;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.post.comment.CommentListInput;
import com.youyu.dto.post.comment.CommentListOutput;
import com.youyu.dto.post.comment.PostReplyListInput;
import com.youyu.dto.common.PageOutput;
import com.youyu.entity.post.Comment;

/**
 * (Comment)表服务接口
 *
 * @author makejava
 * @since 2023-02-12 21:20:24
 */
public interface CommentService extends IService<Comment> {
    PageOutput<CommentListOutput> getCommentsPage(CommentListInput input);

    PageOutput<CommentListOutput> getPostSubCommentsPage(PostReplyListInput input);

    CommentListOutput createPostComment(Comment comment);

    CommentListOutput getCommentDetailById(Long commentId);

    Boolean deleteComment(Long commentId);
}
