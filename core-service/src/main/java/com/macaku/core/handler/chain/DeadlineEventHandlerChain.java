package com.macaku.core.handler.chain;

import com.macaku.core.domain.po.event.DeadlineEvent;
import com.macaku.core.handler.EventHandler;
import com.macaku.core.handler.ext.FirstQuadrantEventHandler;
import com.macaku.core.handler.ext.SecondQuadrantEventHandler;
import com.macaku.core.handler.ext.ThirdQuadrantEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-22
 * Time: 19:49
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DeadlineEventHandlerChain extends EventHandler {

    private final FirstQuadrantEventHandler firstQuadrantEventHandler;

    private final SecondQuadrantEventHandler secondQuadrantEventHandler;

    private final ThirdQuadrantEventHandler thirdQuadrantEventHandler;

    private EventHandler initHandlerChain() {
        firstQuadrantEventHandler.setNextHandler(secondQuadrantEventHandler);
        secondQuadrantEventHandler.setNextHandler(thirdQuadrantEventHandler);
        return firstQuadrantEventHandler;
    }

    @PostConstruct
    public void doPostConstruct() {
        this.setNextHandler(initHandlerChain());
    }
    @Override
    public void handle(DeadlineEvent deadlineEvent, long nowTimestamp) {
        super.doNextHandler(deadlineEvent, nowTimestamp);
        log.warn("责任链处理完毕！");
    }
}
