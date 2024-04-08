package com.macaku.medal.handler.ext;

import com.macaku.medal.domain.entry.VictoryWithinGrasp;
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
 * Time: 12:27
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VictoryWithinGraspHandler extends ApplyMedalHandler {

    private final static Class<VictoryWithinGrasp> MEDAL_ENTRY = VictoryWithinGrasp.class;

    private final static int FULL_VALUE = 100;

    @Value("${medal.victory-within-grasp.id}")
    private Long medalId;

    @Value("${medal.victory-within-grasp.coefficient}")
    private Integer coefficient;

    private final UserMedalService userMedalService;

    private final Function<Long, Integer> getLevelStrategy = credit -> MedalEntryUtil.getLevel(credit, coefficient);


    public int getIncrement(VictoryWithinGrasp victoryWithinGrasp) {
        Integer probability = victoryWithinGrasp.getProbability();
        Integer oldProbability = victoryWithinGrasp.getOldProbability();
        if(oldProbability.equals(FULL_VALUE)) {
            return probability.equals(FULL_VALUE) ? 0 : -1;
        }else {
            return probability.equals(FULL_VALUE) ? 1 : 0;
        }
    }

    @Override
    public void handle(Object object) {
        log.info("{} 尝试处理对象 {}", this.getClass(), object);
        MedalEntryUtil.getMedalEntry(object, MEDAL_ENTRY).ifPresent(victoryWithinGrasp -> {
            // 看看信心指数是否拉满，决定是否计数给用户
            Long userId = victoryWithinGrasp.getUserId();
            UserMedal dbUserMedal = userMedalService.getUserMedal(userId, medalId);
            long credit = Objects.isNull(dbUserMedal) ? 0 : dbUserMedal.getCredit();
            int increment = getIncrement(victoryWithinGrasp);
            if(increment != 0) {
                credit += increment;
                super.saveMedalEntry(userId, medalId, credit, dbUserMedal, getLevelStrategy);
            }
        });
        super.doNextHandler(object);
    }

}
