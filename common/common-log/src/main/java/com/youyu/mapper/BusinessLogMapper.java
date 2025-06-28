package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.BusinessLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (Logs)表数据库访问层
 *
 * @author makejava
 * @since 2025-06-27 23:34:13
 */
@Mapper
@Repository
public interface BusinessLogMapper extends BaseMapper<BusinessLog> {

}

