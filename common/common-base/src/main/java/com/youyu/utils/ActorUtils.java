package com.youyu.utils;

import com.youyu.entity.user.Actor;

import java.util.Map;
import java.util.Optional;

public class ActorUtils {
    /**
     * 从 makeActorMap 中获取具体的Actor信息
     *
     * @param actorId   actor id
     * @param actorType actor类型
     * @param actorMap  actorMap
     * @return Actor
     */
    public static Actor getActorWithMap(Long actorId, int actorType, Map<Integer, Map<Long, Actor>> actorMap) {
        return Optional.ofNullable(actorMap)
                .map(m -> m.get(actorType))
                .map(m -> m.get(actorId))
                .orElse(null);
    }
}
