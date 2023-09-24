package com.youyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.entity.User;
import com.youyu.entity.UserFollow;

import java.util.List;

/**
 * (UserFollow)表服务接口
 *
 * @author makejava
 * @since 2023-03-19 20:39:52
 */
public interface UserFollowService extends IService<UserFollow> {
    boolean isCurrentUserFollow(Long userId);
    List<Long> getFollowUserIdList(Long userId);
    int getUserFollowCount(Long userId);
}
