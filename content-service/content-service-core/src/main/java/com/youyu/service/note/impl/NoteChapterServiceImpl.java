package com.youyu.service.note.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.note.ChapterListOutput;
import com.youyu.dto.note.NoteChapterStatsDTO;
import com.youyu.dto.note.NoteUserOutput;
import com.youyu.dto.note.detail.NoteChapterDetailOutput;
import com.youyu.entity.note.NoteChapter;
import com.youyu.mapper.note.NoteChapterMapper;
import com.youyu.service.note.NoteChapterService;
import com.youyu.service.note.NoteService;
import com.youyu.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * (NoteChapter)表服务实现类
 *
 * @author makejava
 * @since 2023-04-08 22:49:24
 */
@Service("noteChapterService")
public class NoteChapterServiceImpl extends ServiceImpl<NoteChapterMapper, NoteChapter> implements NoteChapterService {

    @Resource
    private NoteChapterMapper noteChapterMapper;

    @Resource
    private NoteService noteService;

    @Override
    public List<NoteChapter> listChapter(Long noteId) {
        LambdaQueryWrapper<NoteChapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NoteChapter::getNoteId, noteId);
        return noteChapterMapper.selectList(queryWrapper);
    }

    @Override
    public List<ChapterListOutput> listChapterByIds(List<Long> chapterIds) {
        if (chapterIds == null || chapterIds.isEmpty()) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<NoteChapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(NoteChapter::getId, chapterIds);
        List<NoteChapter> chapterList = noteChapterMapper.selectList(queryWrapper);
        List<ChapterListOutput> outputs = BeanCopyUtils.copyBeanList(chapterList, ChapterListOutput.class);

        if (outputs.isEmpty()) {
            return outputs;
        }

        // 收集所有需要的用户ID（从 userIds 字段的第一个用户）
        Set<Long> userIdSet = new HashSet<>();
        outputs.forEach(chapter -> {
            if (chapter.getUserIds() != null && !chapter.getUserIds().isEmpty()) {
                String[] idsStr = chapter.getUserIds().split(",");
                if (idsStr.length > 0) {
                    userIdSet.add(Long.valueOf(idsStr[0].trim()));
                }
            }
        });

        // 批量查询用户信息
        List<NoteUserOutput> users = userIdSet.isEmpty()
                ? Collections.emptyList()
                : noteService.getUserDetailByIds(new ArrayList<>(userIdSet), false);
        Map<Long, NoteUserOutput> userMap = users.stream()
                .collect(Collectors.toMap(NoteUserOutput::getId, user -> user));

        // 填充数据
        outputs.forEach(chapter -> {
            if (chapter.getUserIds() != null && !chapter.getUserIds().isEmpty()) {
                String[] idsStr = chapter.getUserIds().split(",");
                if (idsStr.length > 0) {
                    Long userId = Long.valueOf(idsStr[0].trim());
                    NoteUserOutput user = userMap.get(userId);
                    if (user != null) {
                        chapter.setUser(user);
                    }
                }
            }
        });

        return outputs;
    }

    @Override
    public NoteChapterDetailOutput getChapter(Long id) {
        NoteChapter output = noteChapterMapper.selectById(id);
        if (output == null) {
            return null;
        }
        NoteChapterDetailOutput chapter = BeanCopyUtils.copyBean(output, NoteChapterDetailOutput.class);
        String[] stringIds = chapter.getUserIds().split(",");
        List<String> list = Arrays.asList(stringIds);
        List<Long> ids = list.stream().map(Long::valueOf).collect(Collectors.toList());
        List<NoteUserOutput> userList = noteService.getUserDetailByIds(ids, false);
        chapter.setUsers(userList);
        // 浏览量+1
        output.setViewCount(output.getViewCount() + 1);
        int update = noteChapterMapper.updateById(output);
        return chapter;
    }

    public void setExtraData(ChapterListOutput chapter) {
        String[] idsStr = chapter.getUserIds().split(",");
        List<String> list = Arrays.asList(idsStr);
        List<Long> ids = list.stream().map(Long::valueOf).collect(Collectors.toList());
        NoteUserOutput user = noteService.getUserDetailById(ids.get(0), false);
        chapter.setUser(user);
    }

    @Override
    public Map<Long, NoteChapterStatsDTO> batchGetChapterStats(List<Long> noteIds) {
        if (noteIds == null || noteIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<NoteChapterStatsDTO> statsList = noteChapterMapper.batchGetChapterStats(noteIds);
        return statsList.stream()
                .collect(Collectors.toMap(NoteChapterStatsDTO::getNoteId, stats -> stats));
    }
}
