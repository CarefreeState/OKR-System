package com.macaku.center.websocket.redis;

import com.macaku.center.websocket.service.WebSocketService;
import com.macaku.redis.repository.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-25
 * Time: 20:46
 */
@RequiredArgsConstructor
@Component
public class WebSocketRedis {

    private final RedisCache redisCache;

    public void put(String key, WebSocketService webSocketService) {
        redisCache.setCacheObject(key, webSocketService);
    }

    public WebSocketService get(String key) {
        return (WebSocketService) redisCache.getCacheObject(key).orElse(null);
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

}
