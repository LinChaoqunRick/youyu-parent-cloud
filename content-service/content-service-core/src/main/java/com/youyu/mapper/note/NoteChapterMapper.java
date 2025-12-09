package com.youyu.mapper.note;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.note.NoteChapter;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (NoteChapter)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-08 22:49:24
 */
@Mapper
@Repository
public interface NoteChapterMapper extends BaseMapper<NoteChapter> {

}

