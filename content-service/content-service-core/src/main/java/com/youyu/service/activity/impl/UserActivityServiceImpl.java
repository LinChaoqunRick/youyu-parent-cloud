package com.youyu.service.activity.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.comparator.CompareUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.dto.activity.UserActivitiesInput;
import com.youyu.dto.moment.MomentListOutput;
import com.youyu.dto.note.ChapterListOutput;
import com.youyu.dto.note.NoteListOutput;
import com.youyu.dto.page.PageOutput;
import com.youyu.dto.post.PostListOutput;
import com.youyu.entity.activity.UserActivity;
import com.youyu.mapper.activity.UserActivityMapper;
import com.youyu.service.activity.UserActivityService;
import com.youyu.service.moment.MomentService;
import com.youyu.service.note.NoteChapterService;
import com.youyu.service.note.NoteService;
import com.youyu.service.post.PostService;
import com.youyu.utils.PageUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户活动服务实现类
 */
@Service
public class UserActivityServiceImpl implements UserActivityService {

    @Resource
    private UserActivityMapper userActivityMapper;

    @Resource
    private PostService postService;

    @Resource
    private MomentService momentService;

    @Resource
    private NoteService noteService;

    @Resource
    private NoteChapterService noteChapterService;

    @Override
    public PageOutput<Object> listUserActivities(UserActivitiesInput input) {
        Page<UserActivity> page = new Page<>(input.getPageNum(), input.getPageSize());
        IPage<UserActivity> activities = userActivityMapper.listUserActivities(page, input);

        Map<Integer, List<UserActivity>> collect = activities.getRecords().stream()
                .collect(Collectors.groupingBy(UserActivity::getType));
        List<Object> resultList = new ArrayList<>();

        collect.keySet().forEach(key -> {
            if (key == 1) { // 文章
                List<Long> postIds = collect.get(key).stream()
                        .map(UserActivity::getId)
                        .collect(Collectors.toList());
                List<PostListOutput> postList = postService.postListByIds(postIds);
                resultList.addAll(postList);
            } else if (key == 2) { // 时刻
                List<Long> momentIds = collect.get(key).stream()
                        .map(UserActivity::getId)
                        .collect(Collectors.toList());
                List<MomentListOutput> momentList = momentService.momentListByIds(momentIds);
                resultList.addAll(momentList);
            } else if (key == 3) { // 笔记
                List<Long> noteIds = collect.get(key).stream()
                        .map(UserActivity::getId)
                        .collect(Collectors.toList());
                List<NoteListOutput> noteList = noteService.noteListByIds(noteIds);
                resultList.addAll(noteList);
            } else if (key == 4) { // 章节
                List<Long> chapterIds = collect.get(key).stream()
                        .map(UserActivity::getId)
                        .collect(Collectors.toList());
                List<ChapterListOutput> chapterList = noteChapterService.listChapterByIds(chapterIds);
                resultList.addAll(chapterList);
            }
        });

        PageOutput<Object> output = PageUtils.setPageResult(page, Object.class);
        // 使用 hutool 工具类按创建时间降序排序
        resultList.sort((a, b) -> {
            Date aTime = (Date) BeanUtil.getFieldValue(a, "createTime");
            Date bTime = (Date) BeanUtil.getFieldValue(b, "createTime");
            return CompareUtil.compare(bTime, aTime);
        });
        output.setList(resultList);
        return output;
    }
}