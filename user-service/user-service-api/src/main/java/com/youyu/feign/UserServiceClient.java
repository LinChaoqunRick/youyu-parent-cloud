package com.youyu.feign;

import com.youyu.dto.*;
import com.youyu.dto.RegionData;
import com.youyu.dto.VisitData;
import com.youyu.dto.page.PageOutput;
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
    ResponseResult<UserDTO> selectById(@RequestParam Long userId);

    @PostMapping(value = "/user/open/getActorById")
    ResponseResult<Actor> getActorById(@RequestParam Long actorId, @RequestParam int actorType);

    @PostMapping(value = "/visitor/open/getVisitorByEmail")
    ResponseResult<VisitorDTO> getVisitorByEmail(@RequestParam String email);

    @PostMapping(value = "/visitor/open/saveOrUpdateByEmail")
    ResponseResult<VisitorDTO> saveOrUpdateByEmail(@RequestBody VisitorDTO visitor);

    @PostMapping(value = "/visitor/open/selectBatchIds")
    ResponseResult<List<VisitorDTO>> selectBatchIds(@RequestBody List<Long> ids);

    @PostMapping(value = "/user/open/getActors")
    ResponseResult<Map<Integer, Map<Long, Actor>>> getActors(@RequestBody List<ActorBase> actorBases);

    @PostMapping(value = "/user/open/listByIds")
    ResponseResult<List<UserDTO>> listByIds(@RequestParam("userIds") List<Long> userIds);

    @PostMapping(value = "/user/follow/open/getFansCount")
    ResponseResult<Integer> getFansCount(@RequestParam Long userId);

    @PostMapping(value = "/user/follow/isFollow")
    ResponseResult<Integer> isFollow(@RequestParam Long userId, @RequestParam Long userIdTo);

    @PostMapping(value = "/user/open/getProfileMenu")
    ResponseResult<ProfileMenuDTO> getProfileMenu(@RequestParam Long userId);

    @PostMapping(value = "/user/follow/open/getUserFollowCount")
    ResponseResult<Integer> getUserFollowCount(@RequestParam Long userId);

    @PostMapping(value = "/user/follow/getFollowUserIdList")
    ResponseResult<List<Long>> getFollowUserIdList(@RequestParam Long userId);

    @PostMapping(value = "/user/follow/open/isCurrentUserFollow")
    ResponseResult<Boolean> isCurrentUserFollow(@RequestParam Long userId);

    @RequestMapping("/user/open/pageUserByUserIds")
    ResponseResult<PageOutput<UserDTO>> pageUserByUserIds(@RequestParam long current, @RequestParam long size, @RequestParam List<Long> userIds);

    @PostMapping(value = "/visitor/open/getVisitorTotal")
    ResponseResult<Long> getVisitorTotal();

    @PostMapping(value = "/visitor/open/getMonthlyNewVisitors")
    ResponseResult<List<VisitData>> getMonthlyNewVisitors();

    @PostMapping(value = "/visitor/open/getVisitorsByProvince")
    ResponseResult<List<RegionData>> getVisitorsByProvince();

    @PostMapping(value = "/user/open/selectCountByEmail")
    ResponseResult<Integer> selectCountByEmail(@RequestParam String email);

    @PostMapping(value = "/user/open/selectCountByUsername")
    ResponseResult<Integer> selectCountByUsername(@RequestParam String username);

    @PostMapping(value = "/user/open/getActorEmailById")
    ResponseResult<String> getActorEmailById(@RequestParam Long actorId, @RequestParam int actorType);
}
