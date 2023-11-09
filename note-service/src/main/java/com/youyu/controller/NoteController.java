package com.youyu.controller;

import com.youyu.dto.common.PageOutput;
import com.youyu.dto.detail.NoteDetailOutput;
import com.youyu.dto.list.NoteListInput;
import com.youyu.dto.list.NoteListOutput;
import com.youyu.entity.Note;

import com.youyu.result.ResponseResult;
import com.youyu.service.NoteChapterService;
import com.youyu.service.NoteService;
import com.youyu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

/**
 * (Note)表控制层
 *
 * @author makejava
 * @since 2023-04-05 17:35:20
 */
@RestController
@RequestMapping("/note")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private UserService userService;

    @Autowired
    private NoteChapterService noteChapterService;

    @RequestMapping("/create")
    public ResponseResult<Boolean> createNote(@Valid Note note) {
        boolean save = noteService.save(note);
        return ResponseResult.success(save);
    }

    @RequestMapping("/list")
    public ResponseResult<PageOutput<NoteListOutput>> noteList(NoteListInput input) {
        PageOutput<NoteListOutput> pageOutput = noteService.noteList(input);
        return ResponseResult.success(pageOutput);
    }

    @RequestMapping("/get")
    public ResponseResult<NoteDetailOutput> getNote(Long noteId) {
        NoteDetailOutput note = noteService.getNote(noteId);
        return ResponseResult.success(note);
    }

    @RequestMapping("/update")
    public ResponseResult<Boolean> getNote(@Valid Note note) {
        note.setUpdateTime(new Date());
        boolean update = noteService.updateById(note);
        return ResponseResult.success(update);
    }
}

