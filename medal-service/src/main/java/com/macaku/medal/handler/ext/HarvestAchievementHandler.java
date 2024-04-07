package com.macaku.medal.handler.ext;

import com.macaku.medal.domain.entry.HarvestAchievement;
import com.macaku.medal.handler.ApplyMedalHandler;
import com.macaku.medal.handler.util.MedalEntryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 12:25
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HarvestAchievementHandler extends ApplyMedalHandler {

    private final static Class<HarvestAchievement> MEDAL_ENTRY = HarvestAchievement.class;

    @Override
    public void handle(Object object) {
        MedalEntryUtil.getMedalEntry(object, MEDAL_ENTRY).ifPresent(harvestAchievement -> {
            // 将完成度换算成积分给用户



        });
        super.doNextHandler(object);
    }

}
