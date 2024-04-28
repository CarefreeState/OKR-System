package com.macaku.user.sse.util;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.user.sse.component.SeeSessionMapSelector;
import com.macaku.user.sse.util.session.SseSessionMap;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-26
 * Time: 15:34
 */
public class SseSessionMapper {

    private final static SseSessionMap SESSION_MAP = SpringUtil.getBean(SeeSessionMapSelector.class).select();

    public static void put(String sessionKey, SseEmitter sseEmitter) {
        SESSION_MAP.put(sessionKey, sseEmitter);
    }

    public static SseEmitter get(String sessionKey) {
        return SESSION_MAP.get(sessionKey);
    }

    public static boolean containsKey(String sessionKey) {
        return SESSION_MAP.containsKey(sessionKey);
    }

    public static void remove(String sessionKey) {
        SESSION_MAP.remove(sessionKey);
    }

    public static int size(String prefix) {
        return SESSION_MAP.size(prefix);
    }

    public static Set<String> getKeys(String prefix) {
        return SESSION_MAP.getKeys(prefix);
    }

    public static void consumeKey(String sessionKey, Consumer<SseEmitter> consumer) {
        consumer.accept(get(sessionKey));
    }

    public static void consumePrefix(String prefix, Consumer<SseEmitter> consumer) {
        getKeys(prefix).stream().parallel().forEach(sessionKey -> {
            consumeKey(sessionKey, consumer);
        });
    }

    public static void removeAll(String prefix) {
        getKeys(prefix).stream().parallel().forEach(SseSessionMapper::remove);
    }

}
