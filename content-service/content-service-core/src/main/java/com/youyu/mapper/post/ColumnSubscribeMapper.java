package com.youyu.mapper.post;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.user.ColumnSubscribe;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (ColumnSubscribe)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-23 21:30:39
 */
@Mapper
@Repository
public interface ColumnSubscribeMapper extends BaseMapper<ColumnSubscribe> {

}

