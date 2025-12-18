package com.youyu.service.moment.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.UserDTO;
import com.youyu.dto.moment.MomentUserOutput;
import com.youyu.dto.page.PageOutput;
import com.youyu.dto.moment.MomentLikeUserListInput;
import com.youyu.entity.moment.Moment;
import com.youyu.entity.moment.MomentLike;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.feign.UserServiceClient;
import com.youyu.mapper.moment.MomentLikeMapper;
import com.youyu.mapper.moment.MomentMapper;
import com.youyu.service.moment.MomentLikeService;
import com.youyu.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * (MomentLike)表服务实现类
 *
 * @author makejava
 * @since 2023-07-02 11:20:04
 */
@Service("momentLikeService")
@Slf4j
public class MomentLikeServiceImpl extends ServiceImpl<MomentLikeMapper, MomentLike> implements MomentLikeService {

    @Resource
    private MomentLikeMapper momentLikeMapper;

    @Resource
    private UserServiceClient userServiceClient;

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
        Long count = momentLikeMapper.selectCount(queryWrapper);
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
        if (userIds.isEmpty()) {
            return null;
        }
        // 分页查询
        PageOutput<UserDTO> postPage = userServiceClient.pageUserByUserIds(input.getPageNum(), input.getPageSize(), userIds).getData();
        // 封装查询结果 - 手动转换类型
        List<MomentUserOutput> momentUserOutputs = BeanUtil.copyToList(postPage.getList(), MomentUserOutput.class);
        return new PageOutput<>(momentUserOutputs, postPage.getCurrent(), postPage.getPages(), postPage.getSize(), postPage.getTotal());
    }

    @Override
    public void rectifySupportCount() {
        List<Moment> commentList = momentMapper.selectList(null);
        commentList.forEach(moment -> {
            LambdaQueryWrapper<MomentLike> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MomentLike::getMomentId, moment.getId());
            Long count = momentLikeMapper.selectCount(queryWrapper);
            moment.setSupportCount(count);
            momentMapper.updateById(moment);
        });
    }

    @Override
    public Set<Long> getLikedMomentIds(Long userId, List<Long> momentIds) {
        if (userId == null || momentIds == null || momentIds.isEmpty()) {
            return Collections.emptySet();
        }
        LambdaQueryWrapper<MomentLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MomentLike::getUserId, userId);
        queryWrapper.in(MomentLike::getMomentId, momentIds);
        queryWrapper.select(MomentLike::getMomentId);
        return momentLikeMapper.selectList(queryWrapper).stream()
                .map(MomentLike::getMomentId)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<Long, List<MomentUserOutput>> batchGetLikeUsers(List<Long> momentIds, int limit) {
        if (momentIds == null || momentIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 查询所有点赞记录
        LambdaQueryWrapper<MomentLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(MomentLike::getMomentId, momentIds);
        queryWrapper.select(MomentLike::getMomentId, MomentLike::getUserId, MomentLike::getCreateTime);
        queryWrapper.orderByDesc(MomentLike::getCreateTime);
        List<MomentLike> likes = momentLikeMapper.selectList(queryWrapper);

        // 按 momentId 分组
        Map<Long, List<MomentLike>> likesByMoment = likes.stream()
                .collect(Collectors.groupingBy(MomentLike::getMomentId));

        // 收集所有用户ID
        Set<Long> allUserIds = likes.stream()
                .map(MomentLike::getUserId)
                .collect(Collectors.toSet());

        // 批量查询用户信息
        Map<Long, UserDTO> userMap = Collections.emptyMap();
        if (!allUserIds.isEmpty()) {
            List<UserDTO> users = userServiceClient.listByIds(new ArrayList<>(allUserIds)).getData();
            userMap = users.stream().collect(Collectors.toMap(UserDTO::getId, u -> u));
        }

        // 组装结果
        Map<Long, List<MomentUserOutput>> result = new HashMap<>();
        Map<Long, UserDTO> finalUserMap = userMap;
        likesByMoment.forEach((momentId, momentLikes) -> {
            List<MomentUserOutput> likeUsers = momentLikes.stream()
                    .limit(limit)
                    .map(like -> {
                        UserDTO user = finalUserMap.get(like.getUserId());
                        return user != null ? BeanUtil.copyProperties(user, MomentUserOutput.class) : null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            result.put(momentId, likeUsers);
        });

        return result;
    }
}

