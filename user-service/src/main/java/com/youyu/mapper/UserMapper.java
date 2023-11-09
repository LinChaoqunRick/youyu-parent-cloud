package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.auth.Route;
import com.youyu.entity.user.User;
import com.youyu.entity.auth.UserFramework;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (User)表数据库访问层
 *
 * @author makejava
 * @since 2023-02-10 21:06:54
 */
@Mapper
@Repository
public interface UserMapper extends BaseMapper<User> {
    List<Route> getAuthRoutes(@Param("id") Long id);
    List<Route> getRoutesByRoleId(@Param("roleId") Long roleId);
    UserFramework getUserById(@Param("id") Long id);
}

