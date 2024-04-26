package com.macaku.center.websocket.session;

import javax.websocket.Session;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-26
 * Time: 0:22
 */
public interface SessionMap {

    void put(String key, Session session);

    Session get(String key);

    boolean containsKey(String key);

    void remove(String key);

    int size(String prefix);

    Set<String> getKeys(String prefix);

    void consumePrefix(String prefix, Consumer<Session> consumer);

    void consumeKey(String key, Consumer<Session> consumer);

}
