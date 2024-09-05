package com.macaku.user.sse.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-26
 * Time: 16:37
 */
@Repository
@Slf4j
public class SseSessionMap {

    private final static Map<String, SseEmitter> sessionMap = new ConcurrentHashMap<>();

    public void put(String key, SseEmitter sseEmitter) {
        sessionMap.put(key, sseEmitter);
    }

    public SseEmitter get(String key) {
        return sessionMap.get(key);
    }

    public boolean containsKey(String key) {
        return sessionMap.containsKey(key);
    }

    public void remove(String key) {
        sessionMap.remove(key);
    }

    public int size(String prefix) {
        return getKeys(prefix).size();
    }

    public Set<String> getKeys(String prefix) {
        return sessionMap.entrySet().stream()
                .map(Map.Entry::getKey)
                .parallel()
                .filter(key -> key.matches(String.format("^%s.*", prefix)))
                .collect(Collectors.toSet());
    }

    public void consumePrefix(String prefix, Consumer<SseEmitter> consumer) {
        getKeys(prefix).stream()
                .parallel()
                .forEach(key -> {
                    consumeKey(key, consumer);
                });
    }

    public void consumeKey(String key, Consumer<SseEmitter> consumer) {
        consumer.accept(get(key));
    }

}
