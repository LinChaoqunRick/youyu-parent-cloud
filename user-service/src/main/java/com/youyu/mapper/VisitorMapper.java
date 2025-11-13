package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.user.Visitor;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (Visitor)表数据库访问层
 *
 * @author makejava
 * @since 2025-09-16 17:22:50
 */
@Mapper
@Repository
public interface VisitorMapper extends BaseMapper<Visitor> {

}

