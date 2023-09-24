package com.youyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.MomentLikeUserListInput;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.user.UserListOutput;
import com.youyu.entity.*;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.mapper.MomentLikeMapper;
import com.youyu.mapper.MomentMapper;
import com.youyu.mapper.UserMapper;
import com.youyu.service.MomentLikeService;
import com.youyu.utils.PageUtils;
import com.youyu.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * (MomentLike)表服务实现类
 *
 * @author makejava
 * @since 2023-07-02 11:20:04
 */
@Service("momentLikeService")
public class MomentLikeServiceImpl extends ServiceImpl<MomentLikeMapper, MomentLike> implements MomentLikeService {

    @Resource
    private MomentLikeMapper momentLikeMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private MomentMapper momentMapper;

    @Override
    public boolean setMomentLike(MomentLike input) {
        int insert = momentLikeMapper.insert(input);
        if (insert > 0) {
            Moment moment = momentMapper.selectById(input.getMomentId());
            moment.setSupportCount(moment.getSupportCount() + 1);
            momentMapper.updateById(moment);
            return true;
        } else {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
    }

    @Override
    public boolean isMomentLike(MomentLike input) {
        LambdaQueryWrapper<MomentLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MomentLike::getMomentId, input.getMomentId());
        queryWrapper.eq(MomentLike::getUserId, SecurityUtils.getUserId());
        int count = momentLikeMapper.selectCount(queryWrapper);
        return count > 0;
    }

    @Override
    public boolean cancelMomentLike(MomentLike input) {
        LambdaQueryWrapper<MomentLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MomentLike::getMomentId, input.getMomentId());
        queryWrapper.eq(MomentLike::getUserId, SecurityUtils.getUserId());
        int delete = momentLikeMapper.delete(queryWrapper);
        if (delete > 0) {
            Moment moment = momentMapper.selectById(input.getMomentId());
            moment.setSupportCount(moment.getSupportCount() - 1);
            momentMapper.updateById(moment);
            return true;
        } else {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
    }

    @Override
    public PageOutput<MomentUserOutput> likeUsers(MomentLikeUserListInput input) {
        LambdaQueryWrapper<MomentLike> likeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        likeLambdaQueryWrapper.eq(MomentLike::getMomentId, input.getMomentId());
        likeLambdaQueryWrapper.select(MomentLike::getUserId);
        List<MomentLike> momentLikes = momentLikeMapper.selectList(likeLambdaQueryWrapper);
        List<Long> userIds = momentLikes.stream().map(MomentLike::getUserId).collect(Collectors.toList());
        if (userIds.size() == 0) {
            return null;
        }

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.in(User::getId, userIds);

        // 分页查询
        Page<User> page = new Page<>(input.getPageNum(), input.getPageSize());
        Page<User> postPage = userMapper.selectPage(page, userLambdaQueryWrapper);

        // 封装查询结果
        return PageUtils.setPageResult(postPage, MomentUserOutput.class);
    }

    @Override
    public void rectifySupportCount() {
        List<Moment> commentList = momentMapper.selectList(null);
        commentList.forEach(moment -> {
            LambdaQueryWrapper<MomentLike> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MomentLike::getMomentId, moment.getId());
            Integer count = momentLikeMapper.selectCount(queryWrapper);
            moment.setSupportCount(count);
            momentMapper.updateById(moment);
        });
    }
}

