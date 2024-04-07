package com.macaku.core.init.handler;

import com.macaku.core.domain.po.event.DeadlineEvent;

import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-12
 * Time: 9:44
 */
public abstract class EventHandler {

    private EventHandler eventHandler;

    public abstract void handle(DeadlineEvent deadlineEvent, long nowTimestamp);

    public void setNextHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    protected void doNextHandler(DeadlineEvent deadlineEvent, long nowTimestamp) {
        if(Objects.nonNull(eventHandler)) {
            eventHandler.handle(deadlineEvent, nowTimestamp);
        }
    }
}
