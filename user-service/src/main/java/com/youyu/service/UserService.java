package com.youyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.user.*;
import com.youyu.entity.auth.Route;
import com.youyu.entity.user.PositionInfo;
import com.youyu.entity.user.User;
import com.youyu.entity.user.UserDetailOutput;
import com.youyu.entity.auth.UserFramework;

import java.util.List;

/**
 * (User)表服务接口
 *
 * @author makejava
 * @since 2023-02-10 21:05:48
 */
public interface UserService extends IService<User> {
    PageOutput<UserListOutput> list(UserListInput input);

    PageOutput<UserListOutput> followList(UserFollowListInput input);

    PageOutput<UserListOutput> fansList(UserFansListInput input);

    UserDetailOutput detail(Long userId);

    List<Route> getAuthRoutes(Long id);

    List<Route> getManageAuthRoutes(Long id);

    List<Route> getRoutesByRoleId(Long roleId);

    UserFramework getUserById(Long id);

    PageInfo<Object> getUserDynamics(DynamicListInput input);
}
