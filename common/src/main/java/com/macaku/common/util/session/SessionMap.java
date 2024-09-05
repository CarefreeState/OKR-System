package com.macaku.common.util.session;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-05
 * Time: 14:25
 */
public class SessionMap <T> extends ConcurrentHashMap<String, T> {

    public T put(String key, T data) {
        return super.put(key, data);
    }

    public T get(String key) {
        return super.get(key);
    }

    public boolean containsKey(String key) {
        return super.containsKey(key);
    }

    public void remove(String key) {
        super.remove(key);
    }

    public int size(String prefix) {
        return getKeys(prefix).size();
    }

    public Set<String> getKeys(String prefix) {
        return super.entrySet().stream()
                .map(Map.Entry::getKey)
                .parallel()
                .filter(key -> key.matches(String.format("^%s.*", prefix)))
                .collect(Collectors.toSet());
    }

    public void consumeKey(String key, Consumer<T> consumer) {
        consumer.accept(this.get(key));
    }

    public void consumePrefix(String prefix, Consumer<T> consumer) {
        getKeys(prefix).stream().parallel().forEach(sessionKey -> {
            consumeKey(sessionKey, consumer);
        });
    }

    public void removeAll(String prefix) {
        getKeys(prefix).stream().parallel().forEach(this::remove);
    }


}
