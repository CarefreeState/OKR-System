package com.macaku.user.websocket.util.session.impl;

import com.macaku.user.websocket.util.session.SessionMap;
import com.macaku.redis.repository.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
// 哭哭，Session 无法序列化！
// 分布式的话，为每个微服务都请求一遍，保证全局性？
@Repository
@RequiredArgsConstructor
public class SessionRedisMap implements SessionMap {

    private final RedisCache redisCache;

    @Override
    public void put(String key, Session webSocketService) {
        redisCache.setCacheObject(key, webSocketService);
    }

    @Override
    public Session get(String key) {
        return (Session) redisCache.getCacheObject(key).orElse(null);
    }

    @Override
    public boolean containsKey(String key) {
        return redisCache.isExists(key);
    }

    @Override
    public void remove(String key) {
        redisCache.deleteObject(key);
    }

    @Override
    public int size(String prefix) {
        return redisCache.getCacheKeysByPrefix(prefix).size();
    }

    @Override
    public Set<String> getKeys(String prefix) {
        return redisCache.getCacheKeysByPrefix(prefix);
    }

    @Override
    public void consumePrefix(String prefix, Consumer<Session> consumer) {
        getKeys(prefix).stream()
                .parallel()
                .forEach(key -> {
                    consumeKey(key, consumer);
                });
    }

    @Override
    public void consumeKey(String key, Consumer<Session> consumer) {
        consumer.accept(get(key));
    }

}
