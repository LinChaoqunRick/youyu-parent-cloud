package com.youyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.mapper.ManageUserMapper;
import com.youyu.entity.user.ManageUser;
import com.youyu.service.ManageUserService;
import org.springframework.stereotype.Service;

/**
 * (ManageUser)表服务实现类
 *
 * @author makejava
 * @since 2025-08-02 09:21:56
 */
@Service("manageUserService")
public class ManageUserServiceImpl extends ServiceImpl<ManageUserMapper, ManageUser> implements ManageUserService {

}

