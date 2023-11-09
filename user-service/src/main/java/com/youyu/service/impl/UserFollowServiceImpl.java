package com.youyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.entity.user.UserFollow;
import com.youyu.mapper.UserFollowMapper;
import com.youyu.service.UserFollowService;
import com.youyu.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * (UserFollow)表服务实现类
 *
 * @author makejava
 * @since 2023-03-19 20:39:52
 */
@Service("userFollowService")
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow> implements UserFollowService {

    @Resource
    private UserFollowMapper userFollowMapper;

    @Override
    public boolean isCurrentUserFollow(Long userId) {
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getUserId, SecurityUtils.getUserId());
        queryWrapper.eq(UserFollow::getUserIdTo, userId);
        Integer count = userFollowMapper.selectCount(queryWrapper);
        return count > 0;
    }

    @Override
    public List<Long> getFollowUserIdList(Long userId) {
        if (Objects.isNull(userId)) {
            userId = SecurityUtils.getUserId();
        }
        LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFollow::getUserId, userId);
        List<UserFollow> followList = userFollowMapper.selectList(queryWrapper);
        List<Long> followIds = followList.stream()
                .map(UserFollow::getUserIdTo)
                .collect(Collectors.toList());
        return followIds;
    }

    @Override
    public int getUserFollowCount(Long userId) {
        LambdaQueryWrapper<UserFollow> userFollowLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userFollowLambdaQueryWrapper.eq(UserFollow::getUserIdTo, userId);
        int fansCount = userFollowMapper.selectCount(userFollowLambdaQueryWrapper);
        return fansCount;
    }
}
