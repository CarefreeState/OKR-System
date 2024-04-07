package com.macaku.medal.handler.ext;

import com.macaku.medal.domain.entry.StandOutCrowd;
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
public class StandOutCrowdHandler extends ApplyMedalHandler {

    private final static Class<StandOutCrowd> MEDAL_ENTRY = StandOutCrowd.class;

    @Value("${medal.stand-out-crowd.id}")
    private Long medalId;

    @Value("${medal.stand-out-crowd.coefficient}")
    private Integer coefficient;

    private final UserMedalService userMedalService;

    private final Function<Long, Integer> getLevelStrategy = credit -> MedalEntryUtil.getLevel(credit, coefficient);

    private int getStandOutCredit(Boolean isAdvance, Integer degree) {
        int count = 0;
        if(Objects.nonNull(isAdvance) && Boolean.TRUE.equals(isAdvance)) {
            count++;
        }
        if(Objects.nonNull(degree) && degree.compareTo(100) > 0) {
            count++;
        }
        return count;
    }

    @Override
    public void handle(Object object) {
        log.info("{} 尝试处理对象 {}", this.getClass().getName(), object);
        MedalEntryUtil.getMedalEntry(object, MEDAL_ENTRY).ifPresent(standOutCrowd -> {
            // 截止时间与现在对比，判断是否超额完成，决定是否计数给用户
            Boolean isAdvance = standOutCrowd.getIsAdvance();
            Integer degree = standOutCrowd.getDegree();
            Long userId = standOutCrowd.getUserId();
            int standOutCredit = getStandOutCredit(isAdvance, degree);
            if(standOutCredit > 0) {
                UserMedal dbUserMedal = userMedalService.getDbUserMedal(userId, medalId);
                long credit = Objects.isNull(dbUserMedal) ? standOutCredit : dbUserMedal.getCredit() + standOutCredit;
                super.saveMedalEntry(userId, medalId, credit, dbUserMedal, getLevelStrategy);
            }
        });
        super.doNextHandler(object);
    }

}
