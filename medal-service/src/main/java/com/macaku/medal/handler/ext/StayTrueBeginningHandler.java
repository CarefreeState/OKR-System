package com.macaku.medal.handler.ext;

import com.macaku.core.domain.po.quadrant.vo.FirstQuadrantVO;
import com.macaku.core.domain.vo.OkrCoreVO;
import com.macaku.core.service.OkrCoreService;
import com.macaku.medal.domain.entry.StayTrueBeginning;
import com.macaku.medal.domain.po.UserMedal;
import com.macaku.medal.handler.ApplyMedalHandler;
import com.macaku.medal.handler.util.MedalEntryUtil;
import com.macaku.medal.service.UserMedalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
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
public class StayTrueBeginningHandler extends ApplyMedalHandler {

    private final static Class<StayTrueBeginning> MEDAL_ENTRY = StayTrueBeginning.class;

    @Value("${medal.stay-true-beginning.id}")
    private Long medalId;

    @Value("${medal.stay-true-beginning.coefficient}")
    private Integer coefficient;

    private final UserMedalService userMedalService;

    private final OkrCoreService okrCoreService;

    private final Function<Long, Integer> getLevelStrategy = credit -> 1;

    private boolean checkNull(Object... objects) {
        for (Object o : objects) {
            if(Objects.isNull(o)) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    private boolean isStayTrueBeginning(Long coreId) {
        OkrCoreVO okrCoreVO = okrCoreService.searchOkrCore(coreId);
        FirstQuadrantVO firstQuadrantVO = okrCoreVO.getFirstQuadrantVO();
        String objective = firstQuadrantVO.getObjective();
        Date firstQuadrantDeadline = firstQuadrantVO.getDeadline();
        Integer secondQuadrantCycle = okrCoreVO.getSecondQuadrantCycle();
        Date secondQuadrantDeadline = okrCoreVO.getSecondQuadrantVO().getDeadline();
        Integer thirdQuadrantCycle = okrCoreVO.getThirdQuadrantCycle();
        Date thirdQuadrantDeadline = okrCoreVO.getThirdQuadrantVO().getDeadline();
        return checkNull(objective, firstQuadrantDeadline, secondQuadrantCycle,
                secondQuadrantDeadline, thirdQuadrantCycle, thirdQuadrantDeadline);
    }

    @Override
    public void handle(Object object) {
        log.info("{} 尝试处理对象 {}", this.getClass(), object);
        MedalEntryUtil.getMedalEntry(object, MEDAL_ENTRY).ifPresent(stayTrueBeginning -> {
            // 判断是否是第一次指定 OKR
            Long userId = stayTrueBeginning.getUserId();
            UserMedal dbUserMedal = userMedalService.getUserMedal(userId, medalId);
            if(Objects.isNull(dbUserMedal)) {
                Long coreId = stayTrueBeginning.getCoreId();
                boolean flag = isStayTrueBeginning(coreId);
                if(Boolean.TRUE.equals(flag)) {
                    super.saveMedalEntry(userId, medalId, 1L, null, getLevelStrategy);
                }
            }
        });
        super.doNextHandler(object);
    }

}
