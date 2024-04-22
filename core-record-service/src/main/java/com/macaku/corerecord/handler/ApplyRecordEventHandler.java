package com.macaku.corerecord.handler;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-22
 * Time: 16:17
 */
@Slf4j
public abstract class ApplyRecordEventHandler {

    private ApplyRecordEventHandler recordEventHandler;

    public abstract void handle(Object object);

    public void setNextHandler(ApplyRecordEventHandler recordEventHandler) {
        this.recordEventHandler = recordEventHandler;
    }

    protected void doNextHandler(Object object) {
        if(Objects.nonNull(recordEventHandler)) {
            recordEventHandler.handle(object);
        }
    }

}
