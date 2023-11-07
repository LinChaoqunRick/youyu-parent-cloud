package com.youyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.user.*;
import com.youyu.entity.Route;
import com.youyu.entity.User;
import com.youyu.entity.UserDetailOutput;
import com.youyu.entity.UserFramework;
import org.apache.ibatis.annotations.Param;

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

    List<Route> getRoutesByRoleId(Long roleId);

    UserFramework getUserById(Long id);
}
