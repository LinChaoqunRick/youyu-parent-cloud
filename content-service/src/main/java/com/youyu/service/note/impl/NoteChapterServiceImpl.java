package com.youyu.service.note.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.note.detail.NoteChapterDetailOutput;
import com.youyu.dto.note.detail.NoteUserOutput;
import com.youyu.dto.note.list.ChapterListOutput;
import com.youyu.entity.note.NoteChapter;
import com.youyu.mapper.note.NoteChapterMapper;
import com.youyu.service.note.NoteChapterService;
import com.youyu.service.note.NoteService;
import com.youyu.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
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
    public List<ChapterListOutput> listChapterByIds(List<Long> noteIds) {
        LambdaQueryWrapper<NoteChapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(NoteChapter::getId, noteIds);
        List<NoteChapter> chapterList = noteChapterMapper.selectList(queryWrapper);
        List<ChapterListOutput> outputs = BeanCopyUtils.copyBeanList(chapterList, ChapterListOutput.class);
        outputs.forEach(this::setExtraData);
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
}
