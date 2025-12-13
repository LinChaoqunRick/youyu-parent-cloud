package com.youyu.mapper;

import com.youyu.entity.auth.UserFramework;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface LoginMapper {
    UserFramework getUserForLogin(@Param("username") String username, @Param("email") String email);
}
