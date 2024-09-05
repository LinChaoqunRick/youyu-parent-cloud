package com.youyu.service;

import com.youyu.dto.moment.MomentCommentListOutput;
import com.youyu.dto.post.comment.CommentListOutput;

public interface MailService {
    Boolean sendRegisterCode(String target, boolean repeat);
    Boolean sendPostCommentMailNotice(CommentListOutput detail);
    Boolean sendMomentCommentMailNotice(MomentCommentListOutput detail);
}
