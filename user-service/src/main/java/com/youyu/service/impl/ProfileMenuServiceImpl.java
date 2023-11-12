package com.youyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.mapper.ProfileMenuDao;
import com.youyu.entity.user.ProfileMenu;
import com.youyu.service.ProfileMenuService;
import org.springframework.stereotype.Service;

/**
 * (ProfileMenu)表服务实现类
 *
 * @author makejava
 * @since 2023-05-07 12:13:44
 */
@Service("profileMenuService")
public class ProfileMenuServiceImpl extends ServiceImpl<ProfileMenuDao, ProfileMenu> implements ProfileMenuService {

}

