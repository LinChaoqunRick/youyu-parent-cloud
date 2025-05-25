package com.youyu.service.mail.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.entity.post.PostLike;
import com.youyu.mapper.post.PostLikeMapper;
import com.youyu.service.post.PostLikeService;
import org.springframework.stereotype.Service;

/**
 * (PostLike)表服务实现类
 *
 * @author makejava
 * @since 2023-02-19 17:01:44
 */
@Service("postLikeService")
public class PostLikeServiceImpl extends ServiceImpl<PostLikeMapper, PostLike> implements PostLikeService {

}
