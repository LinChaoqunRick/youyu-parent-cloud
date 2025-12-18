package com.youyu.mapper.note;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.dto.note.NoteChapterStatsDTO;
import com.youyu.entity.note.NoteChapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (NoteChapter)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-08 22:49:24
 */
@Mapper
@Repository
public interface NoteChapterMapper extends BaseMapper<NoteChapter> {

    /**
     * 批量查询笔记的章节统计信息（章节数量和总浏览量）
     * @param noteIds 笔记ID列表
     * @return 章节统计信息列表
     */
    List<NoteChapterStatsDTO> batchGetChapterStats(@Param("noteIds") List<Long> noteIds);
}

