package com.macaku.core.init;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class StartedEventListener implements ApplicationListener<ApplicationStartedEvent> {

    public final static String QUADRANT_ID = "id";

    public final static String QUADRANT_DEADLINE = "deadline";

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        System.out.println("应用启动完成，通知监听器执行缓存预加载操作");
    }
}
