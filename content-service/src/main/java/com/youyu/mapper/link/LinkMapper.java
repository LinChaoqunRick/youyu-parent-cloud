package com.youyu.mapper.link;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.link.Link;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (Link)表数据库访问层
 *
 * @author makejava
 * @since 2025-09-11 15:26:54
 */
@Mapper
@Repository
public interface LinkMapper extends BaseMapper<Link> {

}

