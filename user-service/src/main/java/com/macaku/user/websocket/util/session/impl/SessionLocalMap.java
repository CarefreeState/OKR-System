package com.macaku.user.websocket.util.session.impl;

import com.macaku.user.websocket.util.session.SessionMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.websocket.Session;
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
public class SessionLocalMap implements SessionMap {

    private final static Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void put(String key, Session session) {
        sessionMap.put(key, session);
    }

    @Override
    public Session get(String key) {
        return sessionMap.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return sessionMap.containsKey(key);
    }

    @Override
    public void remove(String key) {
        sessionMap.remove(key);
    }

    @Override
    public int size(String prefix) {
        return getKeys(prefix).size();
    }

    @Override
    public Set<String> getKeys(String prefix) {
        return sessionMap.entrySet().stream()
                .map(Map.Entry::getKey)
                .parallel()
                .filter(key -> key.matches(String.format("^%s.*", prefix)))
                .collect(Collectors.toSet());
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
