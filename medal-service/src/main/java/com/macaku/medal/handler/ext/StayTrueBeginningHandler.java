package com.macaku.medal.handler.ext;

import com.macaku.medal.domain.entry.StayTrueBeginning;
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
 * Time: 12:26
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StayTrueBeginningHandler extends ApplyMedalHandler {

    private final static Class<StayTrueBeginning> MEDAL_ENTRY = StayTrueBeginning.class;

    @Override
    public void handle(Object object) {
        MedalEntryUtil.getMedalEntry(object, MEDAL_ENTRY).ifPresent(stayTrueBeginning -> {
            // 判断是否是第一次指定 OKR

        });
        super.doNextHandler(object);
    }

}
