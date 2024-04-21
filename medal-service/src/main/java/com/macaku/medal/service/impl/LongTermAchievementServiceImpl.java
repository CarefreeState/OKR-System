package com.macaku.medal.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.medal.component.TermAchievementServiceSelector;
import com.macaku.medal.domain.entry.LongTermAchievement;
import com.macaku.medal.handler.chain.MedalHandlerChain;
import com.macaku.medal.service.TermAchievementService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 23:14
 */
@Slf4j
public class LongTermAchievementServiceImpl implements TermAchievementService {

    private final static Integer OPTION = TermAchievementServiceSelector.ACTION_OPTION;

    private final MedalHandlerChain medalHandlerChain = SpringUtil.getBean(MedalHandlerChain.class);

    @Override
    public boolean match(Integer option) {
        return OPTION.equals(option);
    }

    @Override
    public void issueTermAchievement(Long userId, Boolean isCompleted, Boolean oldCompleted) {
        LongTermAchievement longTermAchievement = LongTermAchievement.builder()
                .userId(userId).isCompleted(isCompleted).oldCompleted(oldCompleted).build();
        medalHandlerChain.handle(longTermAchievement);
    }

}
