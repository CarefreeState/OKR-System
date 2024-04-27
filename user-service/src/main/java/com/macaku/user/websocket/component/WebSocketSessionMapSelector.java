package com.macaku.user.websocket.component;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.user.websocket.util.session.SessionMap;
import com.macaku.user.websocket.util.session.impl.SessionLocalMap;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-26
 * Time: 16:52
 */
@Component
public class WebSocketSessionMapSelector {

    private final ServiceLoader<SessionMap> sessionMaps = ServiceLoader.load(SessionMap.class);

    public SessionMap select() {
        // 选取服务
        Iterator<SessionMap> iterator = sessionMaps.iterator();
        return iterator.hasNext() ? iterator.next() : SpringUtil.getBean(SessionLocalMap.class);
    }

}
