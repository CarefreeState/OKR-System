package com.macaku.corerecord.handler.chain;

import com.macaku.corerecord.handler.ApplyRecordEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-22
 * Time: 16:27
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RecordEventHandlerChain extends ApplyRecordEventHandler {

    // 这里并不会注入本身 RecordEventHandlerChain，因为这个 Bean 还没有被注入容器（没有循环依赖）
    private final List<ApplyRecordEventHandler> applyRecordEventHandlers;

    private ApplyRecordEventHandler initHandlerChain() {
        int size = applyRecordEventHandlers.size();
        if(size == 0) {
            return null;
        }
        for (int i = 0; i < size - 1; i++) {
            applyRecordEventHandlers.get(i).setNextHandler(applyRecordEventHandlers.get(i + 1));
        }
        return applyRecordEventHandlers.get(0);
    }

    @PostConstruct
    public void doPostConstruct() {
        this.setNextHandler(initHandlerChain());
    }

    @Override
    public void handle(Object object) {
        super.doNextHandler(object);
        log.warn("责任链处理完毕！");
    }
}
