package com.macaku.medal.handler.ext;

import com.macaku.medal.domain.entry.ShortTermAchievement;
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
 * Time: 12:26
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ShortTermAchievementHandler extends ApplyMedalHandler {

    private final static Class<ShortTermAchievement> MEDAL_ENTRY = ShortTermAchievement.class;

    @Value("${medal.short-term-achievement.id}")
    private Long medalId;

    @Value("${medal.short-term-achievement.coefficient}")
    private Integer coefficient;

    private final UserMedalService userMedalService;

    private final Function<Long, Integer> getLevelStrategy = credit -> MedalEntryUtil.getLevel(credit, coefficient);

    @Override
    public void handle(Object object) {
        log.info("{} 尝试处理对象 {}", this.getClass().getName(), object);
        MedalEntryUtil.getMedalEntry(object, MEDAL_ENTRY).ifPresent(shortTermAchievement -> {
            // 任务是否完成，决定是否计数给用户
            Boolean isCompleted = shortTermAchievement.getIsCompleted();
            Long userId = shortTermAchievement.getUserId();
            UserMedal dbUserMedal = userMedalService.getDbUserMedal(userId, medalId);
            long credit = Objects.isNull(dbUserMedal) ? 0 : dbUserMedal.getCredit();
            credit += Boolean.TRUE.equals(isCompleted) ? 1 : -1;
            super.saveMedalEntry(userId, medalId, credit, dbUserMedal, getLevelStrategy);
        });
        super.doNextHandler(object);
    }

}
