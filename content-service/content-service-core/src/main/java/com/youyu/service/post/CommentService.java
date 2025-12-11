package com.youyu.service.post;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.comment.CommentListInput;
import com.youyu.dto.comment.CommentListOutput;
import com.youyu.dto.page.PageOutput;
import com.youyu.entity.post.Comment;

/**
 * (Comment)表服务接口
 *
 * @author makejava
 * @since 2023-02-12 21:20:24
 */
public interface CommentService extends IService<Comment> {
    PageOutput<CommentListOutput> getCommentsPage(CommentListInput input);
    CommentListOutput createComment(Comment comment);
    CommentListOutput getCommentById(Long commentId);
    Boolean deleteComment(Long commentId);
}
