package com.youyu.service;

import com.youyu.dto.login.ResultUser;
import com.youyu.entity.Route;
import com.youyu.entity.UserFramework;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.List;

public interface LoginService {

//    ResultUser login(UserFramework user) throws HttpRequestMethodNotSupportedException;

//    ResultUser loginTelephone(String telephone, String code);

    void logout();

    List<Route> getAuthRoutes(Long id);

    List<Route> getRoutesByRoleId(Long roleId);

    int register(String nickname, String username, String email, String password, String code, int type);

    UserFramework getUserById(Long id);

    String refreshToken();
}
