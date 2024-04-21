package com.macaku.medal.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.medal.component.TermAchievementServiceSelector;
import com.macaku.medal.domain.entry.ShortTermAchievement;
import com.macaku.medal.handler.chain.MedalHandlerChain;
import com.macaku.medal.service.TermAchievementService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 23:15
 */
@Slf4j
public class ShortTermAchievementServiceImpl implements TermAchievementService {

    private final static Integer OPTION1 = TermAchievementServiceSelector.PRIORITY_ONE_OPTION;

    private final static Integer OPTION2 = TermAchievementServiceSelector.PRIORITY_TWO_OPTION;

    private final MedalHandlerChain medalHandlerChain = SpringUtil.getBean(MedalHandlerChain.class);

    @Override
    public boolean match(Integer option) {
        return OPTION1.equals(option) || OPTION2.equals(option);
    }

    @Override
    public void issueTermAchievement(Long userId, Boolean isCompleted, Boolean oldCompleted) {
        ShortTermAchievement shortTermAchievement = ShortTermAchievement.builder()
                .userId(userId).isCompleted(isCompleted).oldCompleted(oldCompleted).build();
        medalHandlerChain.handle(shortTermAchievement);
    }

}
