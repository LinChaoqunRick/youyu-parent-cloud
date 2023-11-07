package com.youyu.service;

public interface LoginService {

    void logout();

    int register(String nickname, String username, String email, String password, String code, int type);

}
