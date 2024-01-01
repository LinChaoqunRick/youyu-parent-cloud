package com.youyu.service;

import com.youyu.dto.ConnectRegisterInput;
import com.youyu.dto.RegisterInput;
import com.youyu.entity.auth.UserFramework;

public interface LoginService {

    void logout();

    int register(RegisterInput input);

    UserFramework connectRegister(ConnectRegisterInput input);

}
