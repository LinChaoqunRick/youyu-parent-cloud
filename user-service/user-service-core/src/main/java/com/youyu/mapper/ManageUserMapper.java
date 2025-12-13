package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.user.ManageUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (ManageUser)表数据库访问层
 *
 * @author makejava
 * @since 2025-08-02 09:21:50
 */
@Mapper
@Repository
public interface ManageUserMapper extends BaseMapper<ManageUser> {

}

