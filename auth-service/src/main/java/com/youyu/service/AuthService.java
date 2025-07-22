package com.youyu.service;

import com.youyu.entity.LoginUser;
import com.youyu.entity.auth.UserFramework;
import com.youyu.entity.auth.AuthParamsEntity;

/**
 * @description 认证接口
 * @version 1.0
 */
public interface AuthService {

  /**
   * @description 认证方法
   * @param authParamsEntity 认证参数
   */
  LoginUser execute(AuthParamsEntity authParamsEntity, String clientId);
}
