package com.youyu.service.moment.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.Actor;
import com.youyu.dto.UserDTO;
import com.youyu.dto.moment.*;
import com.youyu.dto.page.PageOutput;
import com.youyu.entity.moment.Moment;
import com.youyu.entity.moment.MomentLike;
import com.youyu.enums.ActorType;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.feign.UserServiceClient;
import com.youyu.mapper.moment.MomentLikeMapper;
import com.youyu.mapper.moment.MomentMapper;
import com.youyu.service.moment.MomentCommentService;
import com.youyu.service.moment.MomentLikeService;
import com.youyu.service.moment.MomentService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.LocateUtils;
import com.youyu.utils.PageUtils;
import com.youyu.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.*;
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
    private UserServiceClient userServiceClient;

    @Resource
    private MomentMapper momentMapper;

    @Resource
    @Lazy
    private MomentCommentService momentCommentService;

    @Resource
    private MomentLikeMapper momentLikeMapper;

    @Resource
    private MomentLikeService momentLikeService;

    @Override
    public MomentListOutput create(Moment input) {
        int insert = momentMapper.insert(input);
        return getMoment(input.getId());
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
            throw new SystemException(ResultCode.FORBIDDEN);
        }
    }

    @Override
    public PageOutput<MomentListOutput> getMomentList(MomentListInput input) {
        Long currentUserId = SecurityUtils.getUserId();

        // 1. 查询基础分页数据（使用安全的 orderBy）
        LambdaQueryWrapper<Moment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(input.getUserIds())) {
            List<Long> userIds = Arrays.stream(input.getUserIds().split(",")).map(Long::parseLong).toList();
            lambdaQueryWrapper.in(Moment::getUserId, userIds);
        }
        // 使用安全的 orderBy（已验证白名单）
        String validatedOrderBy = input.getValidatedOrderBy();
        lambdaQueryWrapper.last("order by " + validatedOrderBy + " " + (input.isAsc() ? "asc" : "desc"));

        Page<Moment> page = new Page<>(input.getPageNum(), input.getPageSize());
        Page<Moment> postPage = momentMapper.selectPage(page, lambdaQueryWrapper);
        PageOutput<MomentListOutput> pageOutput = PageUtils.setPageResult(postPage, MomentListOutput.class);

        if (pageOutput.getList().isEmpty()) {
            return pageOutput;
        }

        // 2. 收集所有需要的ID
        List<Long> momentIds = pageOutput.getList().stream()
                .map(MomentListOutput::getId)
                .collect(Collectors.toList());
        List<Long> userIds = pageOutput.getList().stream()
                .map(MomentListOutput::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 3. 批量查询用户信息
        List<UserDTO> users = userServiceClient.listByIds(userIds).getData();
        Map<Long, MomentUserOutput> userMap = users.stream()
                .collect(Collectors.toMap(UserDTO::getId,
                    user -> BeanCopyUtils.copyBean(user, MomentUserOutput.class)));

        // 4. 批量查询评论数量
        Map<Long, Integer> commentCountMap = momentCommentService.batchGetCommentCount(momentIds);

        // 5. 批量查询点赞状态
        Set<Long> likedMomentIds = currentUserId != null
                ? momentLikeService.getLikedMomentIds(currentUserId, momentIds)
                : Collections.emptySet();

        // 6. 批量查询点赞用户列表
        Map<Long, List<MomentUserOutput>> likeUsersMap = momentLikeService.batchGetLikeUsers(momentIds, 10);

        // 7. 填充数据
        pageOutput.getList().forEach(moment -> {
            // 设置用户信息
            MomentUserOutput user = userMap.get(moment.getUserId());
            if (user != null) {
                moment.setUser(user);
            }

            // 设置评论数量
            moment.setCommentCount(commentCountMap.getOrDefault(moment.getId(), 0));

            // 设置点赞状态
            moment.setMomentLike(likedMomentIds.contains(moment.getId()));

            // 设置地址名称
            moment.setAdname(LocateUtils.getShortNameByCode(String.valueOf(moment.getAdcode())));

            // 设置点赞用户列表
            List<MomentUserOutput> likeUsers = likeUsersMap.get(moment.getId());
            if (Objects.nonNull(likeUsers)) {
                moment.setLikeUsers(likeUsers);
            }
        });

        return pageOutput;
    }

    @Override
    public PageOutput<MomentListOutput> getMomentListFollow(MomentListInput input) {
        List<Long> userIdList = userServiceClient.getFollowUserIdList(SecurityUtils.getUserId()).getData();
        String idsString = userIdList.stream().map(String::valueOf).collect(Collectors.joining(","));
        input.setUserIds(idsString);
        return getMomentList(input);
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
    public MomentUserOutput getMomentActor(Long actorId, int actorType, boolean enhance) {
        Actor actor = userServiceClient.getActorById(actorId, actorType).getData();
        MomentUserOutput momentUserOutput = BeanCopyUtils.copyBean(actor, MomentUserOutput.class);

        if (enhance && actorType == ActorType.USER.getCode()) {
            MomentUserExtraInfo extraInfo = new MomentUserExtraInfo();

            // 查询时刻数量
            LambdaQueryWrapper<Moment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Moment::getUserId, actorId);
            queryWrapper.select(Moment::getId);
            List<Moment> momentList = momentMapper.selectList(queryWrapper);
            extraInfo.setMomentCount(momentList.size());

            // todo.. 查询点赞数量

            // 查询粉丝数量
            int fansCount = userServiceClient.getUserFollowCount(actorId).getData();
            extraInfo.setFansCount(fansCount);

            momentUserOutput.setExtraInfo(extraInfo);
        }

        if (Objects.nonNull(SecurityUtils.getUserId())) {
            boolean follow = userServiceClient.isCurrentUserFollow(actorId).getData();
            momentUserOutput.setFollow(follow);
        }
        return momentUserOutput;
    }

    @Override
    public boolean isMomentLike(Long momentId) {
        LambdaQueryWrapper<MomentLike> likeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        likeLambdaQueryWrapper.eq(MomentLike::getUserId, SecurityUtils.getUserId());
        likeLambdaQueryWrapper.eq(MomentLike::getMomentId, momentId);
        Long count = momentLikeMapper.selectCount(likeLambdaQueryWrapper);
        return count > 0;
    }

    @Override
    public List<MomentListOutput> momentListByIds(List<Long> momentIds) {
        if (momentIds == null || momentIds.isEmpty()) {
            return Collections.emptyList();
        }

        Long currentUserId = SecurityUtils.getUserId();

        LambdaQueryWrapper<Moment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Moment::getId, momentIds);
        List<Moment> momentList = momentMapper.selectList(queryWrapper);
        List<MomentListOutput> outputList = BeanCopyUtils.copyBeanList(momentList, MomentListOutput.class);

        if (outputList.isEmpty()) {
            return outputList;
        }

        // 收集所有需要的ID
        List<Long> userIds = outputList.stream()
                .map(MomentListOutput::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询用户信息
        List<UserDTO> users = userServiceClient.listByIds(userIds).getData();
        Map<Long, MomentUserOutput> userMap = users.stream()
                .collect(Collectors.toMap(UserDTO::getId,
                    user -> BeanCopyUtils.copyBean(user, MomentUserOutput.class)));

        // 批量查询评论数量
        Map<Long, Integer> commentCountMap = momentCommentService.batchGetCommentCount(momentIds);

        // 批量查询点赞状态
        Set<Long> likedMomentIds = currentUserId != null
                ? momentLikeService.getLikedMomentIds(currentUserId, momentIds)
                : Collections.emptySet();

        // 批量查询点赞用户列表
        Map<Long, List<MomentUserOutput>> likeUsersMap = momentLikeService.batchGetLikeUsers(momentIds, 10);

        // 填充数据
        outputList.forEach(moment -> {
            // 设置用户信息
            MomentUserOutput user = userMap.get(moment.getUserId());
            if (user != null) {
                moment.setUser(user);
            }

            // 设置评论数量
            moment.setCommentCount(commentCountMap.getOrDefault(moment.getId(), 0));

            // 设置点赞状态
            moment.setMomentLike(likedMomentIds.contains(moment.getId()));

            // 设置地址名称
            moment.setAdname(LocateUtils.getShortNameByCode(String.valueOf(moment.getAdcode())));

            // 设置点赞用户列表
            List<MomentUserOutput> likeUsers = likeUsersMap.get(moment.getId());
            if (Objects.nonNull(likeUsers)) {
                moment.setLikeUsers(likeUsers);
            }
        });

        return outputList;
    }

    public void setExtraData(MomentListOutput moment) {
        moment.setUser(getMomentActor(moment.getUserId(), ActorType.USER.getCode(),false));
        moment.setCommentCount(momentCommentService.getCommentCountByMomentId(moment.getId()));
        moment.setMomentLike(isMomentLike(moment.getId()));
        moment.setAdname(LocateUtils.getShortNameByCode(String.valueOf(moment.getAdcode())));
        // 设置点赞用户信息
        MomentLikeUserListInput likeUserListInput = new MomentLikeUserListInput(moment.getId());
        PageOutput<MomentUserOutput> output = momentLikeService.likeUsers(likeUserListInput);
        if (Objects.nonNull(output)) {
            moment.setLikeUsers(output.getList());
        }
    }
}

