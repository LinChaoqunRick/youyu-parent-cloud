package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.user.ProfileMenu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (ProfileMenu)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-07 12:13:44
 */
@Mapper
@Repository
public interface ProfileMenuMapper extends BaseMapper<ProfileMenu> {

}

