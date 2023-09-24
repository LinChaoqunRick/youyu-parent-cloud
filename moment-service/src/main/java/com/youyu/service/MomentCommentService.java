package com.youyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.MomentCommentListInput;
import com.youyu.dto.MomentCommentListOutput;
import com.youyu.dto.MomentReplyListInput;
import com.youyu.dto.common.PageOutput;
import com.youyu.entity.MomentComment;

import java.util.List;

/**
 * (MomentComment)表服务接口
 *
 * @author makejava
 * @since 2023-06-18 20:28:05
 */
public interface MomentCommentService extends IService<MomentComment> {
    MomentCommentListOutput createComment(MomentComment input);
    PageOutput<MomentCommentListOutput> listMomentCommentPage(MomentCommentListInput input);
    List<MomentCommentListOutput> listMomentCommentAll(MomentCommentListInput input);
    MomentCommentListOutput getCommentDetailByCommentId(Long commentId);
    int getReplyCountByMomentId(Long momentId);
    boolean deleteComment(Long momentId);
    PageOutput<MomentCommentListOutput> listMomentReplyPage(MomentReplyListInput input);
}

