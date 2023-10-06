package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.UserFramework;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserFrameworkMapper extends BaseMapper<UserFramework> {
    UserFramework getUserForLogin(@Param("username") String username, @Param("email") String email, @Param("id") Long id);
    UserFramework getUserById(@Param("id") Long id);
    UserFramework getUserByUsername(@Param("username") String username);
    UserFramework getUserByEmail(@Param("email") String email);
}
