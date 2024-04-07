package com.macaku.medal.handler.ext;

import com.macaku.medal.domain.entry.VictoryWithinGrasp;
import com.macaku.medal.handler.ApplyMedalHandler;
import com.macaku.medal.handler.util.MedalEntryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 12:27
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VictoryWithinGraspHandler extends ApplyMedalHandler {

    private final static Class<VictoryWithinGrasp> MEDAL_ENTRY = VictoryWithinGrasp.class;

    @Override
    public void handle(Object object) {
        MedalEntryUtil.getMedalEntry(object, MEDAL_ENTRY).ifPresent(victoryWithinGrasp -> {
            // 看看信心指数是否拉满，决定是否计数给用户
        });
        super.doNextHandler(object);
    }

}
