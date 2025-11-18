package com.youyu.service.actor;

import com.youyu.dto.user.ActorBase;
import com.youyu.entity.user.Actor;
import com.youyu.feign.UserServiceClient;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ActorService {
    @Resource
    private UserServiceClient userServiceClient;

    /**
     * 创建操作者map，便于查询
     *
     * @param actorBases 操作人列表
     * @return 操作人map
     */
    public Map<Integer, Map<Long, Actor>> makeActorMap(List<ActorBase> actorBases) {
        return userServiceClient.getActors(actorBases).getData();
    }

}
