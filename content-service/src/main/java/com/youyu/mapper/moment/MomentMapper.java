package com.youyu.mapper.moment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.moment.Moment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (Moment)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-21 23:22:11
 */
@Mapper
@Repository
public interface MomentMapper extends BaseMapper<Moment> {

}

