package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.user.Column;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (Column)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-13 22:02:29
 */
@Mapper
@Repository
public interface ColumnMapper extends BaseMapper<Column> {


}

