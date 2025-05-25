package com.youyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.moment.MomentListOutput;
import com.youyu.dto.note.list.ChapterListOutput;
import com.youyu.dto.note.list.NoteListOutput;
import com.youyu.dto.post.post.PostListOutput;
import com.youyu.dto.user.*;
import com.youyu.entity.auth.Route;
import com.youyu.entity.auth.UserFramework;
import com.youyu.entity.user.*;
import com.youyu.feign.ContentServiceClient;
import com.youyu.mapper.UserFollowMapper;
import com.youyu.mapper.UserMapper;
import com.youyu.service.UserService;
import com.youyu.utils.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * (User)表服务实现类
 *
 * @author makejava
 * @since 2023-02-10 21:05:48
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserFollowMapper userFollowMapper;

    @Resource
    private ContentServiceClient contentServiceClient;

    @Resource
    private RestTemplate restTemplate;

    @Value("${amap.key}")
    private String amapKey;

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

    @Override
    public PageInfo<Object> getUserDynamics(DynamicListInput input) {
        PageHelper.startPage(input.getPageNum(), input.getPageSize());
        List<DynamicInfo> dynamics = userMapper.getUserDynamics(input);
        PageInfo<DynamicInfo> pageInfo = new PageInfo<>(dynamics);

        Map<Integer, List<DynamicInfo>> collect = pageInfo.getList().stream().collect(Collectors.groupingBy(DynamicInfo::getType));
        List<Object> resultList = new ArrayList<>();

        collect.keySet().forEach(key -> {
            if (key == 1) { // 文章
                List<Long> postIds = collect.get(key).stream().map(DynamicInfo::getId).collect(Collectors.toList());
                List<PostListOutput> postList = contentServiceClient.postListByIds(postIds).getData();
                resultList.addAll(postList);
            } else if (key == 2) { // 时刻
                List<Long> momentIds = collect.get(key).stream().map(DynamicInfo::getId).collect(Collectors.toList());
                List<MomentListOutput> momentList = contentServiceClient.momentListByIds(momentIds).getData();
                resultList.addAll(momentList);
            } else if (key == 3) { // 笔记
                List<Long> noteIds = collect.get(key).stream().map(DynamicInfo::getId).collect(Collectors.toList());
                List<NoteListOutput> noteList = contentServiceClient.noteListByIds(noteIds).getData();
                resultList.addAll(noteList);
            } else if (key == 4) { // 章节
                List<Long> chapterIds = collect.get(key).stream().map(DynamicInfo::getId).collect(Collectors.toList());
                List<ChapterListOutput> chapterList = contentServiceClient.listChapterByIds(chapterIds).getData();
                resultList.addAll(chapterList);
            }
        });

        PageInfo<Object> resultPageInfo = BeanCopyUtils.copyBean(pageInfo, PageInfo.class);
        resultList.sort((a, b) -> {
            Long aTime = ((Date) Objects.requireNonNull(BeanUtils.getFieldValueByFieldName(a, "createTime"))).getTime();
            Long bTime = ((Date) Objects.requireNonNull(BeanUtils.getFieldValueByFieldName(b, "createTime"))).getTime();
            return bTime.compareTo(aTime);
        });
        resultPageInfo.setList(resultList);
        return resultPageInfo;
    }

    @Override
    public PositionInfo getUserPositionByIP() {
        return restTemplate.getForObject("https://restapi.amap.com/v3/ip?key=" + amapKey + "&ip=" + RequestUtils.getClientIp(), PositionInfo.class);
    }

    private void setFollow(Long currentUserId, List<UserListOutput> list) {
        if (!Objects.isNull(currentUserId)) {
            list.forEach(item -> {
                LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(UserFollow::getUserId, currentUserId);
                queryWrapper.eq(UserFollow::getUserIdTo, item.getId());
                Long count = userFollowMapper.selectCount(queryWrapper);
                item.setFollow(count > 0);
            });
        }
    }
}
