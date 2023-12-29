package com.youyu.service;

import com.youyu.dto.RegisterInput;

public interface LoginService {

    void logout();

    int register(RegisterInput input);

}
