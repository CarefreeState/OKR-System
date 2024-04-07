package com.macaku.medal.handler.ext;

import com.macaku.medal.domain.entry.LongTermAchievement;
import com.macaku.medal.domain.po.UserMedal;
import com.macaku.medal.handler.ApplyMedalHandler;
import com.macaku.medal.handler.util.MedalEntryUtil;
import com.macaku.medal.service.UserMedalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

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
public class LongTermAchievementHandler extends ApplyMedalHandler {

    private final static Class<LongTermAchievement> MEDAL_ENTRY = LongTermAchievement.class;

    @Value("${medal.long-term-achievement.id}")
    private Long medalId;

    @Value("${medal.long-term-achievement.coefficient}")
    private Integer coefficient;

    private final UserMedalService userMedalService;

    private final Function<Long, Integer> getLevelStrategy = credit -> MedalEntryUtil.getLevel(credit, coefficient);

    @Override
    public void handle(Object object) {
        log.info("LongTermAchievementHandler 尝试处理对象 {}", object);
        MedalEntryUtil.getMedalEntry(object, MEDAL_ENTRY).ifPresent(longTermAchievement -> {
            // 任务是否完成，决定是否计数给用户
            Boolean isCompleted = longTermAchievement.getIsCompleted();
            Long userId = longTermAchievement.getUserId();
            UserMedal dbUserMedal = userMedalService.getDbUserMedal(userId, medalId);
            long credit = Objects.isNull(dbUserMedal) ? 0 : dbUserMedal.getCredit();
            credit += Boolean.TRUE.equals(isCompleted) ? 1 : -1;
            super.saveMedalEntry(userId, medalId, credit, dbUserMedal, getLevelStrategy);
        });
        super.doNextHandler(object);
    }

}
