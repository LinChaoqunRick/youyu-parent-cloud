package com.youyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.mail.MailReplyInput;
import com.youyu.dto.post.comment.CommentListInput;
import com.youyu.dto.post.comment.CommentListOutput;
import com.youyu.dto.post.comment.PostReplyListInput;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.post.post.PostUserOutput;
import com.youyu.entity.post.Comment;
import com.youyu.entity.post.CommentLike;
import com.youyu.entity.post.Post;
import com.youyu.entity.user.User;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.feign.MailServiceClient;
import com.youyu.feign.UserServiceClient;
import com.youyu.mapper.CommentMapper;
import com.youyu.service.CommentLikeService;
import com.youyu.service.CommentService;
import com.youyu.service.PostService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.PageUtils;
import com.youyu.utils.SecurityUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * (Comment)表服务实现类
 *
 * @author makejava
 * @since 2023-02-12 21:20:32
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private PostService postService;

    @Resource
    private CommentLikeService commentLikeService;

    @Resource
    private UserServiceClient userServiceClient;

    @Resource
    RabbitTemplate template;

    @Override
    public PageOutput<CommentListOutput> getCommentsPage(CommentListInput input) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", input.getPostId());
        queryWrapper.eq("root_id", -1);
        if (StringUtils.hasText(input.getOrderBy())) {
            queryWrapper.orderByDesc(input.getOrderBy());
        }
        // 分页查询
        Page<Comment> page = new Page<>(input.getPageNum(), input.getPageSize());
        page(page, queryWrapper);

        // 封装查询结果
        PageOutput<CommentListOutput> pageOutput = PageUtils.setPageResult(page, CommentListOutput.class);

        List<Long> userIds = pageOutput.getList().stream().map(Comment::getUserId).collect(Collectors.toList());
        Map<Long, PostUserOutput> userMap = createUserMap(userIds);

        setExtraData(pageOutput.getList(), userMap);

        return pageOutput;
    }

    @Override
    public PageOutput<CommentListOutput> getPostSubCommentsPage(PostReplyListInput input) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId, input.getCommentId());
        queryWrapper.last("order by" + " " + input.getOrderBy() + " " + (input.isAsc() ? "asc" : "desc"));
        // 分页查询
        Page<Comment> page = new Page<>(input.getPageNum(), input.getPageSize());
        page(page, queryWrapper);

        // 封装查询结果
        PageOutput<CommentListOutput> outputs = PageUtils.setPageResult(page, CommentListOutput.class);

        List<Long> userIds = new ArrayList<>();
        userIds.addAll(outputs.getList().stream().map(Comment::getUserId).toList());
        userIds.addAll(outputs.getList().stream().map(Comment::getUserIdTo).toList());
        Map<Long, PostUserOutput> userMap = createUserMap(userIds);

        setExtraData(outputs.getList(), userMap);

        return outputs;
    }

    @Override
    public CommentListOutput createComment(@Valid Comment comment) {
        int insert = commentMapper.insert(comment);
        if (insert > 0) {
            CommentListOutput output = getCommentDetailById(comment.getId());
            // 如果回复的是自己，不发送邮件
            if (comment.getUserId().equals(comment.getUserIdTo())) {
                return output;
            }
            User user = userServiceClient.selectById(comment.getUserId()).getData();
            User userTo = userServiceClient.selectById(comment.getUserIdTo()).getData();
            Post post = postService.getById(comment.getPostId());

            // 回复人已绑定邮箱
            if (Objects.nonNull(userTo) && StringUtils.hasText(userTo.getEmail())) {
                MailReplyInput mailReplyInput = new MailReplyInput();
                mailReplyInput.setTarget(userTo.getEmail());
                mailReplyInput.setNickname(userTo.getNickname());
                mailReplyInput.setSubject("[有语] 您有一条新的留言");
                mailReplyInput.setCaption("用户@" + user.getNickname() + " 在你的博客《" + post.getTitle() + "》下留言了：");
                mailReplyInput.setContent(comment.getContent());
                mailReplyInput.setUrl("http://v2.youyul.com/post/details/" + post.getId());
                template.convertAndSend("amq.direct", "postCommentMail", mailReplyInput);
            }

            return output;
        } else {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
    }

    @Override
    public CommentListOutput getCommentDetailById(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        CommentListOutput output = BeanCopyUtils.copyBean(comment, CommentListOutput.class);

        PostUserOutput user = postService.getUserDetailById(comment.getUserId(), false);
        output.setUser(user);

        if (Objects.nonNull(comment.getUserIdTo()) && comment.getUserIdTo() > -1) {
            PostUserOutput userTo = postService.getUserDetailById(comment.getUserIdTo(), false);
            output.setUserTo(userTo);
        }

        return output;

    }

    @Override
    public Boolean deleteComment(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        Post post = postService.getById(comment.getPostId());
        Long authorId = post.getUserId();
        Long currentUserId = SecurityUtils.getUserId();
        Long commentUserId = comment.getUserId();
        if (commentUserId.equals(currentUserId) || authorId.equals(currentUserId)) { // 仅自己和文章的作者可删除
            int delete = commentMapper.deleteById(commentId);
            if (delete > 0) {
                return true;
            } else {
                throw new SystemException(ResultCode.OPERATION_FAIL);
            }
        } else {
            throw new SystemException(ResultCode.FORBIDDEN);
        }
    }

    /**
     * 根据userIds获取map信息
     *
     * @param userIds
     * @return
     */
    public Map<Long, PostUserOutput> createUserMap(List<Long> userIds) {
        Map<Long, PostUserOutput> userMap = new HashMap<>();
        userIds.stream()
                .distinct()
                .filter(id -> id != -1)
                .forEach(id -> {
                    userMap.put(id, postService.getUserDetailById(id, false));
                });
        return userMap;
    }

    public void setExtraData(List<CommentListOutput> list, Map<Long, PostUserOutput> userMap) {
        Long currentUserId = SecurityUtils.getUserId();

        list.forEach(item -> {
            item.setUser(userMap.get(item.getUserId()));
            item.setUserTo(userMap.get(item.getUserIdTo()));

            // 是否点赞了
            if (Objects.nonNull(currentUserId)) {
                CommentLike commentLike = new CommentLike();
                commentLike.setCommentId(item.getId());
                item.setCommentLike(commentLikeService.isPostCommentLike(commentLike));
            }

            // 设置回复数量
            LambdaQueryWrapper<Comment> replyQueryWrapper = new LambdaQueryWrapper<>();
            replyQueryWrapper.eq(Comment::getRootId, item.getId());
            item.setReplyCount(commentMapper.selectCount(replyQueryWrapper));
        });
    }
}
