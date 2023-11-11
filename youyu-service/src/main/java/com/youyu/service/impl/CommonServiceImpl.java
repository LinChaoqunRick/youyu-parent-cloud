package com.youyu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.youyu.dto.DynamicListInput;
import com.youyu.dto.moment.MomentListOutput;
import com.youyu.dto.note.list.ChapterListOutput;
import com.youyu.dto.note.list.NoteListOutput;
import com.youyu.dto.post.post.PostListOutput;
import com.youyu.entity.DynamicInfo;
import com.youyu.mapper.CommonMapper;
import com.youyu.service.*;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service("commonService")
public class CommonServiceImpl implements CommonService {
    @Resource
    private CommonMapper commonMapper;

    @Resource
    private PostService postService;

    @Resource
    private MomentService momentService;

    @Resource
    private NoteService noteService;

    @Resource
    private NoteChapterService noteChapterService;

    @Override
    public PageInfo<Object> getUserDynamics(DynamicListInput input) {
        PageHelper.startPage(input.getPageNum(), input.getPageSize());
        List<DynamicInfo> dynamics = commonMapper.getUserDynamics(input);
        PageInfo<DynamicInfo> pageInfo = new PageInfo<>(dynamics);

        Map<Integer, List<DynamicInfo>> collect = pageInfo.getList().stream().collect(Collectors.groupingBy(DynamicInfo::getType));
        List<Object> resultList = new ArrayList<>();

        collect.keySet().forEach(key -> {
            if (key == 1) { // 文章
                List<Long> postIds = collect.get(key).stream().map(DynamicInfo::getId).collect(Collectors.toList());
                List<PostListOutput> postList = postService.postListByIds(postIds);
                resultList.addAll(postList);
            } else if (key == 2) { // 时刻
                List<Long> momentIds = collect.get(key).stream().map(DynamicInfo::getId).collect(Collectors.toList());
                List<MomentListOutput> momentList = momentService.momentListByIds(momentIds);
                resultList.addAll(momentList);
            } else if (key == 3) { // 笔记
                List<Long> noteIds = collect.get(key).stream().map(DynamicInfo::getId).collect(Collectors.toList());
                List<NoteListOutput> noteList = noteService.noteListByIds(noteIds);
                resultList.addAll(noteList);
            } else if (key == 4) { // 章节
                List<Long> chapterIds = collect.get(key).stream().map(DynamicInfo::getId).collect(Collectors.toList());
                List<ChapterListOutput> chapterList = noteChapterService.listChapterByIds(chapterIds);
                resultList.addAll(chapterList);
            }
        });

        PageInfo<Object> resultPageInfo = BeanCopyUtils.copyBean(pageInfo, PageInfo.class);
        resultList.sort((a, b) -> {
            Long aTime = ((Date) Objects.requireNonNull(BeanUtils.getFieldValueByFieldName(a, "createTime"))).getTime();
            Long bTime = ((Date) Objects.requireNonNull(BeanUtils.getFieldValueByFieldName(b, "createTime"))).getTime();
            return bTime.compareTo(aTime);
        });
        resultPageInfo.setList(resultList);
        return resultPageInfo;
    }
}
