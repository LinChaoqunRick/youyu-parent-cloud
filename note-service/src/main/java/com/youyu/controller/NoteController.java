package com.youyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.detail.NoteDetailOutput;
import com.youyu.dto.list.NoteListInput;
import com.youyu.dto.list.NoteListOutput;
import com.youyu.entity.Note;
import com.youyu.entity.NoteChapter;
import com.youyu.entity.User;
import com.youyu.mapper.NoteMapper;
import com.youyu.result.ResponseResult;
import com.youyu.service.NoteChapterService;
import com.youyu.service.NoteService;
import com.youyu.service.UserService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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

