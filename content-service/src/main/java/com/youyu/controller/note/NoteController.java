package com.youyu.controller.note;

import com.youyu.dto.common.PageOutput;
import com.youyu.dto.note.detail.NoteDetailOutput;
import com.youyu.dto.note.list.NoteListInput;
import com.youyu.dto.note.list.NoteListOutput;
import com.youyu.entity.note.Note;
import com.youyu.result.ResponseResult;
import com.youyu.service.note.NoteChapterService;
import com.youyu.service.note.NoteService;
import com.youyu.utils.SecurityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
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

    @RequestMapping("/delete")
    public ResponseResult<Boolean> deleteNote(@RequestParam Long noteId) {
        Note note = noteService.getById(noteId);
        SecurityUtils.authAuthorizationUser(note.getUserId());
        boolean b = noteService.removeById(noteId);
        return ResponseResult.success(b);
    }

    @RequestMapping("/open/noteListByIds")
    ResponseResult<List<NoteListOutput>> noteListByIds(@RequestParam List<Long> noteIds) {
        List<NoteListOutput> noteListOutputs = noteService.noteListByIds(noteIds);
        return ResponseResult.success(noteListOutputs);
    }
}

