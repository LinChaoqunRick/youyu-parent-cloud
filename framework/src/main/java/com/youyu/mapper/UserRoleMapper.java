package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

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

