package com.youyu.controller;

import com.youyu.dto.common.PageOutput;
import com.youyu.dto.note.detail.NoteDetailOutput;
import com.youyu.dto.note.list.ChapterListOutput;
import com.youyu.dto.note.list.NoteListInput;
import com.youyu.dto.note.list.NoteListOutput;
import com.youyu.entity.note.Note;

import com.youyu.result.ResponseResult;
import com.youyu.service.NoteChapterService;
import com.youyu.service.NoteService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * (Note)表控制层
 *
 * @author makejava
 * @since 2023-04-05 17:35:20
 */
@RestController
@RequestMapping("/note")
public class NoteController {

    @Resource
    private NoteService noteService;

    @Resource
    private NoteChapterService noteChapterService;

    @RequestMapping("/create")
    public ResponseResult<Boolean> createNote(@Valid Note note) {
        boolean save = noteService.save(note);
        return ResponseResult.success(save);
    }

    @RequestMapping("/open/list")
    public ResponseResult<PageOutput<NoteListOutput>> noteList(NoteListInput input) {
        PageOutput<NoteListOutput> pageOutput = noteService.noteList(input);
        return ResponseResult.success(pageOutput);
    }

    @RequestMapping("/open/get")
    public ResponseResult<NoteDetailOutput> getNote(@RequestParam Long noteId) {
        NoteDetailOutput note = noteService.getNote(noteId);
        return ResponseResult.success(note);
    }

    @RequestMapping("/update")
    public ResponseResult<Boolean> getNote(@Valid Note note) {
        note.setUpdateTime(new Date());
        boolean update = noteService.updateById(note);
        return ResponseResult.success(update);
    }

    @RequestMapping("/open/noteListByIds")
    ResponseResult<List<NoteListOutput>> noteListByIds(@RequestParam List<Long> noteIds) {
        List<NoteListOutput> noteListOutputs = noteService.noteListByIds(noteIds);
        return ResponseResult.success(noteListOutputs);
    }
}

