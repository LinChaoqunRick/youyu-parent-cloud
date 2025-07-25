package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.auth.UserFramework;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserFrameworkMapper extends BaseMapper<UserFramework> {
    UserFramework getUserByUsername(@Param("username") String username);
    UserFramework getManageUserByUsername(@Param("username") String username);
    UserFramework getUserByEmail(@Param("email") String email);
    UserFramework getUserByGithubId(@Param("githubId") String githubId);
    UserFramework getUserByQQId(@Param("qqId") String qqId);
}
