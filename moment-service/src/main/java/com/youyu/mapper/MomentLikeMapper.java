package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.MomentLike;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (MomentLike)表数据库访问层
 *
 * @author makejava
 * @since 2023-07-02 11:20:04
 */
@Mapper
@Repository
public interface MomentLikeMapper extends BaseMapper<MomentLike> {

}

