package com.youyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.youyu.dto.comment.CommentListInput;
import com.youyu.dto.comment.CommentListOutput;
import com.youyu.dto.comment.PostReplyListInput;
import com.youyu.dto.common.PageOutput;
import com.youyu.entity.Comment;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.mail.MessagingException;
import java.util.List;

/**
 * (Comment)表服务接口
 *
 * @author makejava
 * @since 2023-02-12 21:20:24
 */
public interface CommentService extends IService<Comment> {
    PageOutput<CommentListOutput> getCommentsPage(CommentListInput input);

    PageOutput<CommentListOutput> getPostSubCommentsPage(PostReplyListInput input);

    CommentListOutput createComment(Comment comment);

    CommentListOutput getCommentDetailById(Long commentId);

    Boolean deleteComment(Long commentId);
}
