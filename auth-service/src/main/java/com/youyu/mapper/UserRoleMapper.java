package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.auth.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (SysUserRole)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-24 21:00:42
 */
@Mapper
@Repository
public interface UserRoleMapper extends BaseMapper<UserRole> {

}

