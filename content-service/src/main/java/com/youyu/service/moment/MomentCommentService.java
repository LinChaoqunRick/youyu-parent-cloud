package com.youyu.service.moment;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.moment.MomentCommentListInput;
import com.youyu.dto.moment.MomentCommentListOutput;
import com.youyu.dto.moment.MomentReplyListInput;
import com.youyu.entity.moment.MomentComment;

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
    MomentCommentListOutput getCommentDetailByCommentId(Long commentId);
    int getCommentCountByMomentId(Long momentId);
    boolean deleteComment(Long momentId);
}

