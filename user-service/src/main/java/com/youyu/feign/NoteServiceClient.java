package com.youyu.feign;

import com.youyu.dto.note.list.ChapterListOutput;
import com.youyu.dto.note.list.NoteListOutput;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "note-service")
public interface NoteServiceClient {
    @PostMapping(value = "/note/open/noteListByIds")
    ResponseResult<List<NoteListOutput>> noteListByIds(@RequestParam List<Long> noteIds);

    @PostMapping(value = "/noteChapter/open/listChapterByIds")
    ResponseResult<List<ChapterListOutput>> listChapterByIds(@RequestParam List<Long> chapterIds);
}
