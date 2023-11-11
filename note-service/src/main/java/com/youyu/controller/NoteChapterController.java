package com.youyu.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youyu.dto.note.detail.NoteChapterDetailOutput;
import com.youyu.entity.note.NoteChapter;
import com.youyu.result.ResponseResult;
import com.youyu.service.NoteChapterService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * (NoteChapter)表控制层
 *
 * @author makejava
 * @since 2023-04-08 22:49:24
 */
@RestController
@RequestMapping("/noteChapter")
public class NoteChapterController {

    @Resource
    private NoteChapterService noteChapterService;

    @RequestMapping("/create")
    public ResponseResult<Boolean> createChapter(@Valid NoteChapter chapter) {
        boolean save = noteChapterService.save(chapter);
        return ResponseResult.success(save);
    }

    @RequestMapping("/list")
    public ResponseResult<List<NoteChapter>> listChapter(Long noteId) {
        List<NoteChapter> list = noteChapterService.listChapter(noteId);
        return ResponseResult.success(list);
    }

    @RequestMapping("/get")
    public ResponseResult<NoteChapterDetailOutput> getChapter(Long id) {
        NoteChapterDetailOutput chapter = noteChapterService.getChapter(id);
        return ResponseResult.success(chapter);
    }

    @RequestMapping("/update")
    public ResponseResult<Boolean> updateChapter(@Valid NoteChapter chapter) {
        LambdaUpdateWrapper<NoteChapter> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(NoteChapter::getId, chapter.getId());
        boolean update = noteChapterService.update(chapter, updateWrapper);
        return ResponseResult.success(update);
    }

    @RequestMapping("/delete")
    public ResponseResult<Boolean> deleteChapter(Long id) {
        boolean delete = noteChapterService.removeById(id);
        return ResponseResult.success(delete);
    }
}

