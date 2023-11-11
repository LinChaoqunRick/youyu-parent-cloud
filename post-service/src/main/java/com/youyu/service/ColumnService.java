package com.youyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.post.column.ColumnListOutput;
import com.youyu.dto.post.column.ColumnPostInput;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.post.post.PostListOutput;
import com.youyu.entity.user.Column;

import java.util.List;

/**
 * (Column)表服务接口
 *
 * @author makejava
 * @since 2023-03-13 22:02:30
 */
public interface ColumnService extends IService<Column> {
    List<ColumnListOutput> getColumnList(Column column);

    List<ColumnListOutput> getColumnListByIds(String[] columnIds);

    ColumnListOutput getColumnDetail(Long columnId);

    ColumnListOutput createColumn(Column column);

    ColumnListOutput updateColumn(Column column);

    Boolean deleteColumn(Long columnId);

    PageOutput<PostListOutput> getColumnPosts(ColumnPostInput input);

    int setColumnIsTop(Long columnId, Boolean isTop);
}
