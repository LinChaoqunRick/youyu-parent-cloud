package com.youyu.service.note.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.UserDTO;
import com.youyu.dto.note.NoteChapterStatsDTO;
import com.youyu.dto.note.NoteListOutput;
import com.youyu.dto.note.NoteUserExtraInfo;
import com.youyu.dto.note.NoteUserOutput;
import com.youyu.dto.page.PageOutput;
import com.youyu.dto.note.detail.NoteDetailOutput;
import com.youyu.dto.note.list.NoteListInput;
import com.youyu.entity.note.Note;
import com.youyu.entity.note.NoteChapter;
import com.youyu.feign.UserServiceClient;
import com.youyu.mapper.note.NoteChapterMapper;
import com.youyu.mapper.note.NoteMapper;
import com.youyu.service.note.NoteChapterService;
import com.youyu.service.note.NoteService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.PageUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * (Note)表服务实现类
 *
 * @author makejava
 * @since 2023-04-05 17:35:24
 */
@Service("noteService")
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements NoteService {

    @Resource
    private NoteMapper noteMapper;

    @Resource
    private NoteChapterMapper noteChapterMapper;

    @Resource
    private UserServiceClient userServiceClient;

    @Override
    public PageOutput<NoteListOutput> noteList(NoteListInput input) {
        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Objects.nonNull(input.getName()), Note::getName, input.getName());
        queryWrapper.eq(Objects.nonNull(input.getUserId()), Note::getUserId, input.getUserId());

        Page<Note> page = new Page<>(input.getPageNum(), input.getPageSize());
        Page<Note> result = noteMapper.selectPage(page, queryWrapper);

        // 封装查询结果
        PageOutput<NoteListOutput> pageOutput = PageUtils.setPageResult(result, NoteListOutput.class);
        setExtraData(pageOutput.getList());
        return pageOutput;
    }

    @Override
    public NoteUserOutput getUserDetailById(Long userId, boolean enhance) {
        UserDTO user = userServiceClient.selectById(userId).getData();
        NoteUserOutput userDetailOutput = BeanCopyUtils.copyBean(user, NoteUserOutput.class);
        if (enhance) {
            setUserExtraData(userDetailOutput);
        }
        return userDetailOutput;
    }

    @Override
    public List<NoteUserOutput> getUserDetailByIds(List<Long> userIds, boolean enhance) {
        List<UserDTO> users = userServiceClient.listByIds(userIds).getData();
        List<NoteUserOutput> outputs = BeanCopyUtils.copyBeanList(users, NoteUserOutput.class);
        if (enhance) {
            outputs.forEach(this::setUserExtraData);
        }
        return outputs;
    }

    @Override
    public NoteDetailOutput getNote(Long noteId) {
        Note note = noteMapper.selectById(noteId);
        NoteDetailOutput output = BeanCopyUtils.copyBean(note, NoteDetailOutput.class);

        // 查询作者
        NoteUserOutput user = getUserDetailById(output.getUserId(), true);
        output.setUser(user);

        // 查询其他信息
        // setExtraData(output);

        return output;
    }

    @Override
    public List<NoteListOutput> noteListByIds(List<Long> noteIds) {
        if (noteIds == null || noteIds.isEmpty()) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Note::getId, noteIds);
        List<Note> noteList = noteMapper.selectList(queryWrapper);
        List<NoteListOutput> outputs = BeanCopyUtils.copyBeanList(noteList, NoteListOutput.class);

        if (outputs.isEmpty()) {
            return outputs;
        }

        // 收集所有需要的ID
        List<Long> userIds = outputs.stream()
                .map(NoteListOutput::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询章节统计信息
        List<NoteChapterStatsDTO> chapterStatsList = noteIds.isEmpty()
                ? Collections.emptyList()
                : noteChapterMapper.batchGetChapterStats(noteIds);
        Map<Long, NoteChapterStatsDTO> chapterStatsMap = chapterStatsList.stream()
                .collect(Collectors.toMap(NoteChapterStatsDTO::getNoteId, stats -> stats));

        // 批量查询用户信息
        List<NoteUserOutput> users = getUserDetailByIds(userIds, true);
        Map<Long, NoteUserOutput> userMap = users.stream()
                .collect(Collectors.toMap(NoteUserOutput::getId, user -> user));

        // 填充数据
        outputs.forEach(note -> {
            // 设置章节统计信息
            NoteChapterStatsDTO stats = chapterStatsMap.get(note.getId());
            if (stats != null) {
                note.setViewCount(stats.getTotalViewCount());
                note.setChapterCount(stats.getChapterCount());
            } else {
                note.setViewCount(0L);
                note.setChapterCount(0);
            }

            // 设置用户信息
            NoteUserOutput user = userMap.get(note.getUserId());
            if (user != null) {
                note.setUser(user);
            }
        });

        return outputs;
    }

    public void setExtraData(List<NoteListOutput> list) {
        list.forEach(this::setExtraData);
    }

    public void setExtraData(NoteListOutput note) {
        LambdaQueryWrapper<NoteChapter> chapterQueryWrapper = new LambdaQueryWrapper<>();
        chapterQueryWrapper.select(NoteChapter::getViewCount);
        chapterQueryWrapper.eq(NoteChapter::getNoteId, note.getId());
        List<NoteChapter> chapterList = noteChapterMapper.selectList(chapterQueryWrapper);
        if (chapterList.size() > 0) {
            long totalViewCount = chapterList.stream()
                    .mapToLong(NoteChapter::getViewCount)
                    .reduce(Long::sum)
                    .getAsLong();
            note.setViewCount(totalViewCount);
            note.setChapterCount(chapterList.size());
        }

        NoteUserOutput userDetailById = getUserDetailById(note.getUserId(), true);
        note.setUser(userDetailById);
    }

    public void setUserExtraData(NoteUserOutput user) {
        NoteUserExtraInfo extraInfo = new NoteUserExtraInfo();
        extraInfo.setFansCount(userServiceClient.getUserFollowCount(user.getId()).getData());
        user.setExtraInfo(extraInfo);
        user.setFollow(userServiceClient.isCurrentUserFollow(user.getId()).getData());
    }
}
