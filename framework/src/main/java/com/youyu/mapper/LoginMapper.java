package com.youyu.mapper;

import com.youyu.entity.Route;
import com.youyu.entity.UserFramework;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface LoginMapper {
    UserFramework getUserForLogin(@Param("username") String username, @Param("email") String email);
    List<Route> getAuthRoutes(@Param("id") Long id);
    List<Route> getRoutesByRoleId(@Param("roleId") Long roleId);
}
