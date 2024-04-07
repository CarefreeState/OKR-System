package com.macaku.medal.handler.chain;

import com.macaku.medal.handler.ApplyMedalHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 16:03
 */
@Component
@RequiredArgsConstructor
public class MedalHandlerChain extends ApplyMedalHandler {

    private final List<ApplyMedalHandler> applyMedalHandlers;

    private ApplyMedalHandler initHandlerChain() {
        int size = applyMedalHandlers.size();
        if(size == 0) {
            return null;
        }
        for (int i = 0; i < size - 1; i++) {
            applyMedalHandlers.get(i).setNextHandler(applyMedalHandlers.get(i + 1));
        }
        return applyMedalHandlers.get(0);
    }

    @PostConstruct
    public void doPostConstruct() {
        this.setNextHandler(initHandlerChain());
    }

    @Override
    public void handle(Object object) {
        super.doNextHandler(object);
    }
}
