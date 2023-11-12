package com.youyu.service;

import com.youyu.entity.auth.UserFramework;
import com.youyu.entity.auth.AuthParamsEntity;

/**
 * @description 认证接口
 * @author Mr.M
 * @date 2022/10/20 14:48
 * @version 1.0
 */
public interface AuthService {

  /**
   * @description 认证方法
   * @param authParamsEntity 认证参数
   * @return 用户信息
   * @author Mr.M
   * @date 2022/9/29 12:11
   */
  UserFramework execute(AuthParamsEntity authParamsEntity);

}
