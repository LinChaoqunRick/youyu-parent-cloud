package com.youyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.user.*;
import com.youyu.entity.*;
import com.youyu.mapper.UserFollowMapper;
import com.youyu.mapper.UserMapper;
import com.youyu.service.UserService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.PageUtils;
import com.youyu.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * (User)表服务实现类
 *
 * @author makejava
 * @since 2023-02-10 21:05:48
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserFollowMapper userFollowMapper;

    @Override
    public PageOutput<UserListOutput> list(UserListInput input) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper
                .like(Objects.nonNull(input.getKey()), User::getNickname, input.getKey())
                .or()
                .like(Objects.nonNull(input.getKey()), User::getId, input.getKey());

        // 分页查询
        Page<User> page = new Page<>(input.getPageNum(), input.getPageSize());
        Page<User> postPage = userMapper.selectPage(page, userLambdaQueryWrapper);

        // 封装查询结果
        PageOutput<UserListOutput> pageOutput = PageUtils.setPageResult(postPage, UserListOutput.class);

        // 当前登录用户的id
        Long currentUserId = SecurityUtils.getUserId();
        setFollow(currentUserId, pageOutput.getList());
        return pageOutput;
    }

    @Override
    public PageOutput<UserListOutput> followList(@Valid UserFollowListInput input) {
        LambdaQueryWrapper<UserFollow> userFollowWrapper = new LambdaQueryWrapper<>();
        userFollowWrapper.eq(UserFollow::getUserId, input.getUserId());
        List<UserFollow> userFollows = userFollowMapper.selectList(userFollowWrapper);
        List<Long> followIds = userFollows.stream()
                .map(user -> user.getUserIdTo())
                .collect(Collectors.toList());
        if (followIds.isEmpty()) {
            return new PageOutput<>();
        }

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.in(User::getId, followIds);

        // 分页查询
        Page<User> page = new Page<>(input.getPageNum(), input.getPageSize());
        Page<User> postPage = userMapper.selectPage(page, userLambdaQueryWrapper);

        // 封装查询结果
        PageOutput<UserListOutput> pageOutput = PageUtils.setPageResult(postPage, UserListOutput.class);

        // 当前登录用户的id
        Long currentUserId = SecurityUtils.getUserId();
        setFollow(currentUserId, pageOutput.getList());
        return pageOutput;
    }

    @Override
    public PageOutput<UserListOutput> fansList(UserFansListInput input) {
        LambdaQueryWrapper<UserFollow> userFollowWrapper = new LambdaQueryWrapper<>();
        userFollowWrapper.eq(UserFollow::getUserIdTo, input.getUserId());
        List<UserFollow> userFollows = userFollowMapper.selectList(userFollowWrapper);
        List<Long> fansIds = userFollows.stream()
                .map(UserFollow::getUserId)
                .collect(Collectors.toList());
        if (fansIds.size() == 0) {
            return new PageOutput<>();
        }

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.in(User::getId, fansIds);

        // 分页查询
        Page<User> page = new Page<>(input.getPageNum(), input.getPageSize());
        Page<User> postPage = userMapper.selectPage(page, userLambdaQueryWrapper);

        // 封装查询结果
        PageOutput<UserListOutput> pageOutput = PageUtils.setPageResult(postPage, UserListOutput.class);

        // 当前登录用户的id
        Long currentUserId = SecurityUtils.getUserId();
        setFollow(currentUserId, pageOutput.getList());
        return pageOutput;
    }

    @Override
    public UserDetailOutput detail(Long userId) {
        User user = userMapper.selectById(userId);
        if (Objects.nonNull(user)) {
            return BeanCopyUtils.copyBean(user, UserDetailOutput.class);
        } else {
            return null;
        }
    }

    @Override
    public List<Route> getAuthRoutes(Long id) {
        return userMapper.getAuthRoutes(id);
    }

    @Override
    public List<Route> getRoutesByRoleId(Long roleId) {
        return userMapper.getRoutesByRoleId(roleId);
    }

    @Override
    public UserFramework getUserById(Long id) {
        UserFramework user = userMapper.getUserById(id);
        return user;
    }

    private void setFollow(Long currentUserId, List<UserListOutput> list) {
        if (!Objects.isNull(currentUserId)) {
            list.forEach(item -> {
                LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(UserFollow::getUserId, currentUserId);
                queryWrapper.eq(UserFollow::getUserIdTo, item.getId());
                Integer count = userFollowMapper.selectCount(queryWrapper);
                item.setFollow(count > 0);
            });
        }
    }
}
