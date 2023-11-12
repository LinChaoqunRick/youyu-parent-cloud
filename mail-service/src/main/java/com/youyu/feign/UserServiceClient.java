package com.youyu.feign;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youyu.entity.user.User;
import com.youyu.result.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "user-service")
public interface UserServiceClient {
    @PostMapping(value = "/user/open/selectCount")
    ResponseResult<Integer> selectCount(LambdaQueryWrapper<User> queryWrapper);

    @PostMapping(value = "/user/open/selectById")
    ResponseResult<User> selectById(Long userId);
}
