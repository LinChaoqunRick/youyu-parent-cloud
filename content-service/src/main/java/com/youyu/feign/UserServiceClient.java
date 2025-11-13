package com.youyu.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.dto.user.ActorBase;
import com.youyu.entity.user.Actor;
import com.youyu.entity.user.ProfileMenu;
import com.youyu.entity.user.User;
import com.youyu.entity.user.Visitor;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(value = "user-service")
public interface UserServiceClient {
    @PostMapping(value = "/user/getUserTotal")
    ResponseResult<Long> getUserTotal();

    @PostMapping(value = "/user/open/selectById")
    ResponseResult<User> selectById(@RequestParam Long userId);

    @PostMapping(value = "/user/open/getActorById")
    ResponseResult<Actor> getActorById(@RequestParam Long actorId, @RequestParam int actorType);

    @PostMapping(value = "/visitor/open/getVisitorByEmail")
    ResponseResult<Visitor> getVisitorByEmail(@RequestParam String email);

    @PostMapping(value = "/visitor/open/saveOrUpdateByEmail")
    ResponseResult<Visitor> saveOrUpdateByEmail(@RequestBody Visitor visitor);

    @PostMapping(value = "/visitor/open/selectBatchIds")
    ResponseResult<List<Visitor>> selectBatchIds(@RequestBody List<Long> ids);

    @PostMapping(value = "/user/open/getActors")
    ResponseResult<Map<Integer, Map<Long, Actor>>> getActors(@RequestBody List<ActorBase> actorBases);

    @PostMapping(value = "/user/open/listByIds")
    ResponseResult<List<User>> listByIds(@RequestParam("userIds") List<Long> userIds);

    @PostMapping(value = "/user/follow/open/getFansCount")
    ResponseResult<Integer> getFansCount(@RequestParam Long userId);

    @PostMapping(value = "/user/follow/isFollow")
    ResponseResult<Integer> isFollow(@RequestParam Long userId, @RequestParam Long userIdTo);

    @PostMapping(value = "/user/open/getProfileMenu")
    ResponseResult<ProfileMenu> getProfileMenu(@RequestParam Long userId);

    @PostMapping(value = "/user/follow/open/getUserFollowCount")
    ResponseResult<Integer> getUserFollowCount(@RequestParam Long userId);

    @PostMapping(value = "/user/follow/getFollowUserIdList")
    ResponseResult<List<Long>> getFollowUserIdList(@RequestParam Long userId);

    @PostMapping(value = "/user/follow/open/isCurrentUserFollow")
    ResponseResult<Boolean> isCurrentUserFollow(@RequestParam Long userId);

    @RequestMapping("/user/open/pageUserByUserIds")
    ResponseResult<Page<User>> pageUserByUserIds(@RequestParam long current, @RequestParam long size, @RequestParam List<Long> userIds);
}
