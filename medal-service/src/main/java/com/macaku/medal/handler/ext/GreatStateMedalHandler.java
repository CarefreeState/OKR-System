package com.macaku.medal.handler.ext;

import com.macaku.core.domain.po.inner.StatusFlag;
import com.macaku.core.mapper.inner.StatusFlagMapper;
import com.macaku.medal.domain.config.StatusFlagConfig;
import com.macaku.medal.domain.entry.GreatState;
import com.macaku.medal.handler.ApplyMedalHandler;
import com.macaku.medal.handler.util.MedalEntryUtil;
import com.macaku.medal.service.UserMedalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
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

    private final StatusFlagConfig statusFlagConfig;

    private final UserMedalService userMedalService;

    private final Function<Long, Integer> getLevelStrategy = credit -> MedalEntryUtil.getLevel(credit, coefficient);

    @Override
    public void handle(Object object) {
        MedalEntryUtil.getMedalEntry(object, MEDAL_ENTRY).ifPresent(greatState -> {
            Long userId = greatState.getUserId();
            // 查看用户当前未完成的个人 OKR 的所有状态指标，算加权平均值
            List<StatusFlag> statusFlags = statusFlagMapper.getStatusFlagsByUserId(userId);
            int size = statusFlags.size();
            long sum = statusFlags
                    .stream()
                    .parallel()
                    .map(statusFlag -> statusFlagConfig.getCredit(statusFlag.getColor()))
                    .reduce(Long::sum)
                    .orElse(0L);
            // 判断是否计数
            double average = (sum * 1.0) / size;
            if (statusFlagConfig.isTouch(average)) {
                super.saveMedalEntry(userId, medalId, (long) average, getLevelStrategy);
            }
        });
        super.doNextHandler(object);
    }

}
