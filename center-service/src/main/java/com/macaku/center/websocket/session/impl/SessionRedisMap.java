package com.macaku.center.websocket.session.impl;

import com.macaku.center.websocket.session.SessionMap;
import com.macaku.redis.repository.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-26
 * Time: 0:23
 */
@Component
@RequiredArgsConstructor
public class SessionRedisMap implements SessionMap {

    private final RedisCache redisCache;

    public void put(String key, Session webSocketService) {
        redisCache.setCacheObject(key, webSocketService);
    }

    public Session get(String key) {
        return (Session) redisCache.getCacheObject(key).orElse(null);
    }

    public boolean containsKey(String key) {
        return redisCache.isExists(key);
    }

    public boolean remove(String key) {
        return redisCache.deleteObject(key);
    }

    public int size(String prefix) {
        return redisCache.getCacheKeysByPrefix(prefix).size();
    }

    @Override
    public Set<String> keysPrefix(String prefix) {
        return redisCache.getCacheKeysByPrefix(prefix);
    }

    @Override
    public void consumePrefix(String prefix, Consumer<Session> consumer) {
        keysPrefix(prefix).stream()
                .parallel()
                .forEach(key -> {
                    consumeKey(key, consumer);
                });
    }

    @Override
    public void consumeKey(String key, Consumer<Session> consumer) {
        consumer.accept((Session)redisCache.getCacheObject(key).orElse(null));
    }

}
