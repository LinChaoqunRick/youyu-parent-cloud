package com.youyu.service;

import com.youyu.entity.Route;
import com.youyu.entity.UserFramework;

import java.util.List;

public interface LoginService {

    void logout();

    List<Route> getAuthRoutes(Long id);

    List<Route> getRoutesByRoleId(Long roleId);

    int register(String nickname, String username, String email, String password, String code, int type);

    UserFramework getUserById(Long id);

    String refreshToken();
}
