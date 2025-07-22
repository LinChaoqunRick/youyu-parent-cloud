package com.youyu.config;

import com.youyu.entity.LoginUser;
import com.youyu.utils.RedisCache;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RedisJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final RedisCache redisCache;

    public RedisJwtGrantedAuthoritiesConverter(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        String clientId = jwt.getAudience().get(0); // 取第一个作为 client_id
        String userId = jwt.getClaimAsString("user_id");
        // 从 Redis 中读取权限列表
        LoginUser loginUser = redisCache.getCacheObject(clientId + ":" + userId);
        List<String> perms = loginUser.getPermissions();
        if (perms == null) {
            return Collections.emptyList();
        }
        // 转为 GrantedAuthority
        return perms.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
