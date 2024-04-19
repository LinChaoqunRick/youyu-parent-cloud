package com.youyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.post.column.ColumnListOutput;
import com.youyu.dto.post.column.ColumnPostInput;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.post.post.PostListOutput;
import com.youyu.dto.post.post.PostUserOutput;
import com.youyu.entity.user.Column;
import com.youyu.entity.user.ColumnSubscribe;
import com.youyu.entity.post.Post;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.mapper.ColumnMapper;
import com.youyu.mapper.ColumnSubscribeMapper;
import com.youyu.mapper.PostMapper;
import com.youyu.service.ColumnService;
import com.youyu.service.PostService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.PageUtils;
import com.youyu.utils.SecurityUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * (Column)表服务实现类
 *
 * @author makejava
 * @since 2023-03-13 22:02:30
 */
@Service("columnService")
public class ColumnServiceImpl extends ServiceImpl<ColumnMapper, Column> implements ColumnService {

    @Resource
    private ColumnMapper columnMapper;

    @Resource
    private PostMapper postMapper;

    @Resource
    @Lazy
    private PostService postService;

    @Resource
    private ColumnSubscribeMapper subscribeMapper;

    @Override
    public List<ColumnListOutput> getColumnList(Column column) {
        LambdaQueryWrapper<Column> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Column::getUserId, column.getUserId());
        queryWrapper.orderByDesc(Column::getIsTop);
        List<Column> list = columnMapper.selectList(queryWrapper);
        List<ColumnListOutput> columnListOutputs = BeanCopyUtils.copyBeanList(list, ColumnListOutput.class);

        columnListOutputs.forEach(this::setExtraData);
        return columnListOutputs;
    }

    @Override
    public List<ColumnListOutput> getColumnListByIds(String[] columnIds) {
        if (Objects.isNull(columnIds)) return null;
        LambdaQueryWrapper<Column> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Column::getId, Arrays.asList(columnIds));
        List<Column> columnList = columnMapper.selectList(queryWrapper);
        List<ColumnListOutput> outputs = BeanCopyUtils.copyBeanList(columnList, ColumnListOutput.class);
        outputs.forEach(this::setExtraData);
        return outputs;
    }

    @Override
    public ColumnListOutput getColumnDetail(Long columnId) {
        if (Objects.isNull(columnId)) return null;
        Column column = columnMapper.selectById(columnId);
        if (Objects.isNull(column)) return null;
        ColumnListOutput columnDetail = BeanCopyUtils.copyBean(column, ColumnListOutput.class);
        setExtraData(columnDetail);
        return columnDetail;
    }

    @Override
    public ColumnListOutput createColumn(@Valid Column column) {
        int insert = columnMapper.insert(column);
        if (insert > 0) {
            return BeanCopyUtils.copyBean(column, ColumnListOutput.class);
        } else {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
    }

    @Override
    public ColumnListOutput updateColumn(Column column) {
        Column column1 = columnMapper.selectById(column.getId());
        if (column1.getUserId().equals(SecurityUtils.getUserId())) {
            int update = columnMapper.updateById(column);
            if (update > 0) {
                return BeanCopyUtils.copyBean(column, ColumnListOutput.class);
            } else {
                throw new SystemException(ResultCode.OPERATION_FAIL);
            }
        } else {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
    }

    @Override
    public Boolean deleteColumn(Long columnId) {
        Column column1 = columnMapper.selectById(columnId);
        if (column1.getUserId().equals(SecurityUtils.getUserId())) {
            int delete = columnMapper.deleteById(columnId);
            if (delete > 0) {
                return true;
            } else {
                throw new SystemException(ResultCode.OPERATION_FAIL);
            }
        } else {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
    }

    @Override
    public PageOutput<PostListOutput> getColumnPosts(ColumnPostInput input) {
        LambdaQueryWrapper<Post> postQueryWrapper = new LambdaQueryWrapper<>();
        postQueryWrapper.like(Post::getColumnIds, input.getColumnId());

        // 分页查询
        Page<Post> page = new Page<>(input.getPageNum(), input.getPageSize());
        Page<Post> postPage = postMapper.selectPage(page, postQueryWrapper);

        // 封装查询结果
        PageOutput<PostListOutput> pageOutput = PageUtils.setPageResult(postPage, PostListOutput.class);
        pageOutput.getList().forEach(post -> postService.setPostListData(post));
        return pageOutput;
    }

    @Override
    public int setColumnIsTop(Long columnId, Boolean isTop) {
        Column column = columnMapper.selectById(columnId);
        SecurityUtils.authAuthorizationUser(column.getUserId());
        column.setIsTop(isTop ? "1" : "0");
        return columnMapper.updateById(column);
    }

    public void setExtraData(ColumnListOutput item) {
        // 设置收录的文章数量
        LambdaQueryWrapper<Post> postQueryWrapper = new LambdaQueryWrapper<>();
        postQueryWrapper.like(Post::getColumnIds, item.getId());
        Long postNum = postMapper.selectCount(postQueryWrapper);
        item.setPostNum(postNum);

        // 设置订阅数量
        LambdaQueryWrapper<ColumnSubscribe> subscribeQueryWrapper = new LambdaQueryWrapper<>();
        subscribeQueryWrapper.eq(ColumnSubscribe::getColumnId, item.getId());
        Long subscriberNum = subscribeMapper.selectCount(subscribeQueryWrapper);
        item.setSubscriberNum(subscriberNum);

        // 设置所属用户信息
        PostUserOutput user = postService.getUserDetailById(item.getUserId(), false);
        item.setUser(user);
    }
}
