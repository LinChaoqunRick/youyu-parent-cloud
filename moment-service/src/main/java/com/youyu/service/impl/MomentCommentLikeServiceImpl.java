package com.youyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.entity.Moment;
import com.youyu.entity.MomentComment;
import com.youyu.entity.MomentLike;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.mapper.MomentCommentLikeMapper;
import com.youyu.entity.MomentCommentLike;
import com.youyu.mapper.MomentCommentMapper;
import com.youyu.mapper.MomentLikeMapper;
import com.youyu.mapper.UserMapper;
import com.youyu.service.MomentCommentLikeService;
import com.youyu.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (MomentCommentLike)表服务实现类
 *
 * @author makejava
 * @since 2023-07-10 22:25:25
 */
@Service("momentCommentLikeService")
public class MomentCommentLikeServiceImpl extends ServiceImpl<MomentCommentLikeMapper, MomentCommentLike> implements MomentCommentLikeService {
    @Resource
    private MomentCommentLikeMapper momentCommentLikeMapper;

    @Resource
    private MomentCommentMapper momentCommentMapper;

    @Override
    public boolean setMomentCommentLike(MomentCommentLike input) {
        input.setUserId(SecurityUtils.getUserId());
        int insert = momentCommentLikeMapper.insert(input);
        if (insert > 0) {
            MomentComment momentComment = momentCommentMapper.selectById(input.getCommentId());
            momentComment.setSupportCount(momentComment.getSupportCount() + 1);
            momentCommentMapper.updateById(momentComment);
            return true;
        } else {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
    }

    @Override
    public boolean isMomentCommentLike(MomentCommentLike input) {
        LambdaQueryWrapper<MomentCommentLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MomentCommentLike::getCommentId, input.getCommentId());
        queryWrapper.eq(MomentCommentLike::getUserId, SecurityUtils.getUserId());
        int count = momentCommentLikeMapper.selectCount(queryWrapper);
        return count > 0;
    }

    @Override
    public boolean cancelMomentCommentLike(MomentCommentLike input) {
        LambdaQueryWrapper<MomentCommentLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MomentCommentLike::getCommentId, input.getCommentId());
        queryWrapper.eq(MomentCommentLike::getUserId, SecurityUtils.getUserId());
        int delete = momentCommentLikeMapper.delete(queryWrapper);
        if (delete > 0) {
            MomentComment momentComment = momentCommentMapper.selectById(input.getCommentId());
            momentComment.setSupportCount(momentComment.getSupportCount() - 1);
            momentCommentMapper.updateById(momentComment);
            return true;
        } else {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
    }

    @Override
    public void rectifySupportCount() {
        List<MomentComment> commentList = momentCommentMapper.selectList(null);
        commentList.forEach(comment -> {
            LambdaQueryWrapper<MomentCommentLike> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MomentCommentLike::getCommentId, comment.getId());
            Integer count = momentCommentLikeMapper.selectCount(queryWrapper);
            comment.setSupportCount(count);
            momentCommentMapper.updateById(comment);
        });
    }
}

