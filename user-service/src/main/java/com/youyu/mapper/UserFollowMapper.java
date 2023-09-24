package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.UserFollow;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (UserFollow)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-19 20:39:51
 */
@Mapper
@Repository
public interface UserFollowMapper extends BaseMapper<UserFollow> {


}

