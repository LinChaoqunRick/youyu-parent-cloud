package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.Note;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (Note)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-05 17:35:20
 */
@Mapper
@Repository
public interface NoteMapper extends BaseMapper<Note> {

}

