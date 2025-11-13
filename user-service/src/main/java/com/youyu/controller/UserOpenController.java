package com.youyu.controller;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Maps;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.annotation.Log;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.user.*;
import com.youyu.entity.auth.Route;
import com.youyu.entity.user.Actor;
import com.youyu.entity.user.ProfileMenu;
import com.youyu.entity.user.User;
import com.youyu.entity.user.Visitor;
import com.youyu.enums.ActorType;
import com.youyu.enums.LogType;
import com.youyu.enums.RoleEnum;
import com.youyu.result.ResponseResult;
import com.youyu.service.ProfileMenuService;
import com.youyu.service.UserService;
import com.youyu.service.VisitorService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.LocateUtils;
import com.youyu.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.util.*;
import java.util.stream.Collectors;

/**
 * (User)表控制层
 *
 * @author makejava
 * @since 2023-02-10 21:05:41
 */
@Slf4j
@RestController
@RequestMapping("/user/open")
public class UserOpenController {

    @Resource
    private UserService userService;

    @Resource
    private VisitorService visitorService;

    @Resource
    private ProfileMenuService profileMenuService;

    @RequestMapping("/list")
    ResponseResult<PageOutput<UserListOutput>> list(UserListInput input) {
        return ResponseResult.success(userService.list(input));
    }

    /**
     * 获取用户主页权限菜单
     *
     * @param userId 用户id
     * @return 权限菜单
     */
    @RequestMapping("/getProfileMenu")
    ResponseResult<ProfileMenu> getProfileMenu(@RequestParam Long userId) {
        LambdaQueryWrapper<ProfileMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProfileMenu::getUserId, userId);
        ProfileMenu menu = profileMenuService.getOne(queryWrapper);
        if (Objects.isNull(menu)) {
            ProfileMenu defaultMenu = new ProfileMenu();
            defaultMenu.setUserId(userId);
            defaultMenu.setId(-1L);
            return ResponseResult.success(defaultMenu);
        }
        return ResponseResult.success(menu);
    }

    /**
     * 获取用户动态
     *
     * @param input 查询
     * @return 动态分页
     */
    @RequestMapping("/listUserActivities")
    ResponseResult<PageOutput<Object>> listUserActivities(@Valid UserActivitiesInput input) {
        input.setAuthorizationUserId(SecurityUtils.getUserId());
        PageOutput<Object> pageInfo = userService.listUserActivities(input);
        return ResponseResult.success(pageInfo);
    }

    @RequestMapping("/followList")
    ResponseResult<PageOutput<UserListOutput>> followList(@Valid UserFollowListInput input) {
        return ResponseResult.success(userService.followList(input));
    }

    @RequestMapping("/fansList")
    ResponseResult<PageOutput<UserListOutput>> fansList(@Valid UserFansListInput input) {
        return ResponseResult.success(userService.fansList(input));
    }

    @RequestMapping("/selectCountByUsername")
    public ResponseResult<Long> selectCountByUsername(@RequestParam String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return ResponseResult.success(userService.count(queryWrapper));
    }

    @RequestMapping("/selectCountByEmail")
    public ResponseResult<Long> selectCountByEmail(@RequestParam String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        return ResponseResult.success(userService.count(queryWrapper));
    }

    @RequestMapping("/selectById")
    public ResponseResult<User> selectById(@RequestParam Long userId) {
        User user = userService.getById(userId);
        user.setAdname(LocateUtils.getShortNameByCode(String.valueOf(user.getAdcode())));
        return ResponseResult.success(user);
    }

    /**
     * 获取单个操作者信息（操作者：用户 or 游客）
     *
     * @param actorId   操作者id
     * @param actorType 操作者类型 0：用户 1：游客
     * @return 操作者信息
     */
    @RequestMapping("/getActorById")
    public ResponseResult<Actor> getActorById(@RequestParam Long actorId, @RequestParam int actorType) {
        Actor actor;
        if (actorType == ActorType.USER.getCode()) {
            User user = userService.getById(actorId);
            actor = BeanCopyUtils.copyBean(user, Actor.class);
        } else {
            Visitor visitor = visitorService.getById(actorId);
            actor = BeanCopyUtils.copyBean(visitor, Actor.class);
        }
        actor.setType(actorType);
        actor.setAdname(LocateUtils.getShortNameByCode(String.valueOf(actor.getAdcode())));
        return ResponseResult.success(actor);
    }

    /**
     * 批量获取操作者信息（操作者：用户 or 游客）
     *
     * @param actorBases 操作者信息列表
     * @return 操作者信息Map
     */
    @RequestMapping("/getActors")
    public ResponseResult<Map<Integer, Map<Long, Actor>>> getActors(@RequestBody List<ActorBase> actorBases) {
        // 过滤用户或游客Id
        List<Long> userIds = actorBases.stream().filter(actor -> actor.getActorType() == ActorType.USER.getCode()).map(ActorBase::getActorId).toList();
        List<Long> visitorIds = actorBases.stream().filter(actor -> actor.getActorType() == ActorType.VISITOR.getCode()).map(ActorBase::getActorId).toList();
        Map<Integer, Map<Long, Actor>> resultMap = Maps.newHashMap();
        if (CollUtil.isNotEmpty(userIds)) {
            Map<Long, Actor> userMap = userService.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, user -> BeanCopyUtils.copyBean(user, Actor.class)));
            resultMap.put(ActorType.USER.getCode(), userMap);
        }
        if (CollUtil.isNotEmpty(visitorIds)) {
            Map<Long, Actor> visitorMap = visitorService.listByIds(visitorIds).stream().collect(Collectors.toMap(Visitor::getId, visitor -> BeanCopyUtils.copyBean(visitor, Actor.class)));
            resultMap.put(ActorType.VISITOR.getCode(), visitorMap);
        }
        return ResponseResult.success(resultMap);
    }

    @RequestMapping("/getUserBasicById")
    public ResponseResult<UserListOutput> getUserBasic(@RequestParam Long userId) {
        User user = userService.getById(userId);
        UserListOutput output = BeanCopyUtils.copyBean(user, UserListOutput.class);
        return ResponseResult.success(output);
    }

    @RequestMapping("/pageUserByUserIds")
    public ResponseResult<Page<User>> pageUserByUserIds(@RequestParam long current, @RequestParam long size, @RequestParam List<Long> userIds) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getId, userIds);
        Page<User> page = userService.page(new Page<>(current, size), queryWrapper);
        return ResponseResult.success(page);
    }

    @RequestMapping("/listByIds")
    public ResponseResult<List<User>> listByIds(@RequestParam("userIds") List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return ResponseResult.success(new ArrayList<>());
        }
        return ResponseResult.success(userService.listByIds(userIds));
    }

    // TODO... 新建文件放置此接口
//    @RequestMapping("/ipLocation")
//    public ResponseResult<TencentLocationResult> getUserPositionInfo() {
//        TencentLocationResult tencentLocationResult = locateUtils.queryTencentIp();
//        // log.info("当前访问IP:{}{}", RequestUtils.getClientIp(), tencentLocationResult.toString());
//        return ResponseResult.success(tencentLocationResult);
//    }

    @RequestMapping("/getAuthRoutes")
    @Log(title = "获取权限路由", type = LogType.ACCESS)
    public ResponseResult<List<Route>> getAuthRoutes() {
        Long userId = SecurityUtils.getUserId();
        if (Objects.isNull(userId)) {
            return ResponseResult.success(userService.getRoutesByRoleId(RoleEnum.NO_LOGGED_USER.getId()));
        } else {
            return ResponseResult.success(userService.getAuthRoutes(userId));
        }
    }
}

