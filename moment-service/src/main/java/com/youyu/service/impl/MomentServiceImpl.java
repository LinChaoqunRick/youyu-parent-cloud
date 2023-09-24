package com.youyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.MomentLikeUserListInput;
import com.youyu.dto.MomentListInput;
import com.youyu.dto.MomentListOutput;
import com.youyu.dto.common.PageOutput;
import com.youyu.entity.*;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.mapper.*;
import com.youyu.service.MomentCommentService;
import com.youyu.service.MomentLikeService;
import com.youyu.service.MomentService;
import com.youyu.service.UserFollowService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.PageUtils;
import com.youyu.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * (Moment)表服务实现类
 *
 * @author makejava
 * @since 2023-05-21 23:22:13
 */
@Service("momentService")
@Slf4j
public class MomentServiceImpl extends ServiceImpl<MomentMapper, Moment> implements MomentService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private MomentMapper momentMapper;

    @Resource
    @Lazy
    private MomentCommentService momentCommentService;

    @Resource
    private UserFollowMapper userFollowMapper;

    @Resource
    private UserFollowService userFollowService;

    @Resource
    private MomentLikeMapper momentLikeMapper;

    @Resource
    private MomentLikeService momentLikeService;

    @Override
    public MomentListOutput create(Moment input) {
        int insert = momentMapper.insert(input);
        MomentListOutput moment = getMoment(input.getId());
        return moment;
    }

    @Override
    public boolean delete(Long momentId) {
        Moment moment = momentMapper.selectById(momentId);
        // 当前登录用户的id
        Long currentUserId = SecurityUtils.getUserId();
        if (moment.getUserId().equals(currentUserId)) {
            int remove = momentMapper.deleteById(momentId);
            if (remove > 0) {
                return true;
            } else {
                throw new SystemException(ResultCode.OPERATION_FAIL);
            }
        } else {
            throw new SystemException(ResultCode.NO_OPERATOR_AUTH);
        }
    }

    @Override
    public PageOutput<MomentListOutput> momentList(MomentListInput input) {
        LambdaQueryWrapper<Moment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(input.getUserIds())) {
            String[] userIds = input.getUserIds().split(",");
            log.info(String.valueOf(userIds));
            lambdaQueryWrapper.in(Moment::getUserId, userIds);
        }
        lambdaQueryWrapper.last("order by" + " " + input.getOrderBy() + " " + (input.isAsc() ? "asc" : "desc"));
        // 分页查询
        Page<Moment> page = new Page<>(input.getPageNum(), input.getPageSize());
        page(page, lambdaQueryWrapper);

        // 封装查询结果
        PageOutput<MomentListOutput> pageOutput = PageUtils.setPageResult(page, MomentListOutput.class);

        // 查询用户信息, 评论数量, 点赞信息
        pageOutput.getList().forEach(this::setExtraData);
        return pageOutput;
    }

    @Override
    public PageOutput<MomentListOutput> momentListFollow(MomentListInput input) {
        List<Long> userIdList = userFollowService.getFollowUserIdList(null);
        String idsString = userIdList.stream().map(String::valueOf).collect(Collectors.joining(","));
        input.setUserIds(idsString);
        return momentList(input);
    }

    @Override
    public MomentListOutput getMoment(Long momentId) {
        Moment moment = momentMapper.selectById(momentId);
        if (Objects.nonNull(moment)) {
            MomentListOutput output = BeanCopyUtils.copyBean(moment, MomentListOutput.class);
            setExtraData(output);
            return output;
        } else {
            throw new SystemException(ResultCode.NOT_FOUND);
        }

    }

    @Override
    public MomentUserOutput getMomentUserDetailById(Long userId, boolean enhance) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        User user = userMapper.selectById(userId);
        MomentUserOutput momentUserOutput = BeanCopyUtils.copyBean(user, MomentUserOutput.class);

        if (enhance) {
            MomentUserExtraInfo extraInfo = new MomentUserExtraInfo();

            // 查询时刻数量
            LambdaQueryWrapper<Moment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Moment::getUserId, userId);
            queryWrapper.select(Moment::getId);
            List<Moment> momentList = momentMapper.selectList(queryWrapper);
            extraInfo.setMomentCount(momentList.size());

            // todo.. 查询点赞数量

            // 查询粉丝数量
            LambdaQueryWrapper<UserFollow> userFollowLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userFollowLambdaQueryWrapper.eq(UserFollow::getUserIdTo, userId);
            int fansCount = userFollowMapper.selectCount(userFollowLambdaQueryWrapper);
            extraInfo.setFansCount(fansCount);

            momentUserOutput.setExtraInfo(extraInfo);
        }

        if (Objects.nonNull(SecurityUtils.getUserId())) {
            boolean follow = userFollowService.isCurrentUserFollow(userId);
            momentUserOutput.setFollow(follow);
        }
        return momentUserOutput;
    }

    @Override
    public boolean isMomentLike(Long momentId) {
        LambdaQueryWrapper<MomentLike> likeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        likeLambdaQueryWrapper.eq(MomentLike::getUserId, SecurityUtils.getUserId());
        likeLambdaQueryWrapper.eq(MomentLike::getMomentId, momentId);
        Integer count = momentLikeMapper.selectCount(likeLambdaQueryWrapper);
        return count > 0;
    }

    @Override
    public List<MomentListOutput> momentListByIds(List<Long> momentIds) {
        LambdaQueryWrapper<Moment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Moment::getId, momentIds);
        List<Moment> momentList = momentMapper.selectList(queryWrapper);
        List<MomentListOutput> outputList = BeanCopyUtils.copyBeanList(momentList, MomentListOutput.class);
        outputList.forEach(this::setExtraData);
        return outputList;
    }

    public void setExtraData(MomentListOutput moment) {
        moment.setUser(getMomentUserDetailById(moment.getUserId(), false));
        moment.setCommentCount(momentCommentService.getReplyCountByMomentId(moment.getId()));
        moment.setMomentLike(isMomentLike(moment.getId()));
        // 设置点赞用户信息
        MomentLikeUserListInput likeUserListInput = new MomentLikeUserListInput(moment.getId());
        PageOutput<MomentUserOutput> output = momentLikeService.likeUsers(likeUserListInput);
        if (Objects.nonNull(output)) {
            moment.setLikeUsers(output.getList());
        }
    }
}

