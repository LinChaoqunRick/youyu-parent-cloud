package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.user.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (Message)表数据库访问层
 *
 * @author makejava
 * @since 2024-02-20 22:03:57
 */
@Mapper
@Repository
public interface MessageMapper extends BaseMapper<Message> {

}

