package com.youyu.feign;

import com.youyu.dto.moment.MomentListOutput;
import com.youyu.dto.note.list.ChapterListOutput;
import com.youyu.dto.note.list.NoteListOutput;
import com.youyu.dto.post.post.PostListOutput;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "content-service")
public interface ContentServiceClient {
    @PostMapping(value = "/post/open/postListByIds")
    ResponseResult<List<PostListOutput>> postListByIds(@RequestParam List<Long> postIds);

    @PostMapping(value = "/moment/open/momentListByIds")
    ResponseResult<List<MomentListOutput>> momentListByIds(@RequestParam List<Long> momentIds);

    @PostMapping(value = "/note/open/noteListByIds")
    ResponseResult<List<NoteListOutput>> noteListByIds(@RequestParam List<Long> noteIds);

    @PostMapping(value = "/noteChapter/open/listChapterByIds")
    ResponseResult<List<ChapterListOutput>> listChapterByIds(@RequestParam List<Long> chapterIds);
}
