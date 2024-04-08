package com.macaku.medal.handler.ext;

import com.macaku.core.domain.po.inner.StatusFlag;
import com.macaku.core.mapper.inner.StatusFlagMapper;
import com.macaku.medal.domain.config.StatusFlagConfig;
import com.macaku.medal.domain.entry.GreatState;
import com.macaku.medal.domain.po.UserMedal;
import com.macaku.medal.handler.ApplyMedalHandler;
import com.macaku.medal.handler.util.MedalEntryUtil;
import com.macaku.medal.service.UserMedalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 12:22
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GreatStateMedalHandler extends ApplyMedalHandler {

    private final static Class<GreatState> MEDAL_ENTRY = GreatState.class;

    @Value("${medal.great-state.id}")
    private Long medalId;

    @Value("${medal.great-state.coefficient}")
    private Integer coefficient;

    private final StatusFlagMapper statusFlagMapper;

    private final UserMedalService userMedalService;

    private final StatusFlagConfig statusFlagConfig;

    private final Function<Long, Integer> getLevelStrategy = credit -> MedalEntryUtil.getLevel(credit, coefficient);

    @Override
    public void handle(Object object) {
        log.info("{} 尝试处理对象 {}", this.getClass(), object);
        MedalEntryUtil.getMedalEntry(object, MEDAL_ENTRY).ifPresent(greatState -> {
            Long userId = greatState.getUserId();
            // 查看用户当前未完成的个人 OKR 的所有状态指标，算加权平均值
            double average = statusFlagConfig.calculateStatusFlag(userId);
            // 判断是否计数
            log.info("用户 {} 状态指标评估： {}", userId, average);
            if (statusFlagConfig.isTouch(average)) {
                UserMedal dbUserMedal = userMedalService.getUserMedal(userId, medalId);
                long credit = Objects.isNull(dbUserMedal) ? 1 : dbUserMedal.getCredit() + 1;
                super.saveMedalEntry(userId, medalId, credit, dbUserMedal, getLevelStrategy);
            }
        });
        super.doNextHandler(object);
    }

}
