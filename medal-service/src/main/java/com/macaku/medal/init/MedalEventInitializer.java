package com.macaku.medal.init;

import com.macaku.medal.handler.chain.MedalHandlerChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 16:19
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MedalEventInitializer implements ApplicationListener<ApplicationStartedEvent> {

    private final MedalHandlerChain medalHandlerChain;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
//        medalHandlerChain.handle(GreatState.builder().userId(1L).build());
    }

}
