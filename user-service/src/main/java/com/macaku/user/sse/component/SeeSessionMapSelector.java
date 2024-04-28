package com.macaku.user.sse.component;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.user.sse.util.session.SseSessionMap;
import com.macaku.user.sse.util.session.impl.SseSessionLocalMap;
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
public class SeeSessionMapSelector {

    private final ServiceLoader<SseSessionMap> sessionMaps = ServiceLoader.load(SseSessionMap.class);

    public SseSessionMap select() {
        // 选取服务
        Iterator<SseSessionMap> iterator = sessionMaps.iterator();
        return iterator.hasNext() ? iterator.next() : SpringUtil.getBean(SseSessionLocalMap.class);
    }

}
