package com.macaku.core.init;

import com.macaku.core.domain.po.event.DeadlineEvent;
import com.macaku.core.init.handler.EventHandler;
import com.macaku.core.init.handler.ext.FirstQuadrantEventHandler;
import com.macaku.core.init.handler.ext.SecondQuadrantEventHandler;
import com.macaku.core.init.handler.ext.ThirdQuadrantEventHandler;
import com.macaku.core.mapper.OkrCoreMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DeadlineEventInitializer implements ApplicationListener<ApplicationStartedEvent> {

    private final OkrCoreMapper okrCoreMapper;

    private final FirstQuadrantEventHandler firstQuadrantEventHandler;

    private final SecondQuadrantEventHandler secondQuadrantEventHandler;

    private final ThirdQuadrantEventHandler thirdQuadrantEventHandler;

    private final EventHandler handlerChain;

    public DeadlineEventInitializer(final OkrCoreMapper okrCoreMapper,
                                    final FirstQuadrantEventHandler firstQuadrantEventHandler,
                                    final SecondQuadrantEventHandler secondQuadrantEventHandler,
                                    final ThirdQuadrantEventHandler thirdQuadrantEventHandler) {
        this.okrCoreMapper = okrCoreMapper;
        this.firstQuadrantEventHandler = firstQuadrantEventHandler;
        this.secondQuadrantEventHandler = secondQuadrantEventHandler;
        this.thirdQuadrantEventHandler = thirdQuadrantEventHandler;
        this.handlerChain = initHandlerChain();
    }

    private EventHandler initHandlerChain() {
        firstQuadrantEventHandler.setNextHandler(secondQuadrantEventHandler);
        secondQuadrantEventHandler.setNextHandler(thirdQuadrantEventHandler);
        return firstQuadrantEventHandler;
    }

    private void handleEvent(DeadlineEvent deadlineEvent) {
        final long nowTimestamp = System.currentTimeMillis();// 当前时间
        handlerChain.handle(deadlineEvent, nowTimestamp);
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.warn("--> --> --> 应用启动成功 --> 开始恢复定时任务 --> --> -->");
        // 获取定时任务
        List<DeadlineEvent> deadlineEvents = okrCoreMapper.getDeadlineEvents();
        // 处理定时任务
        deadlineEvents.stream()
                .parallel()
                .forEach(this::handleEvent);
        log.warn("<-- <-- <-- <-- <-- 定时任务恢复成功 <-- <-- <-- <-- <--");
    }
}
