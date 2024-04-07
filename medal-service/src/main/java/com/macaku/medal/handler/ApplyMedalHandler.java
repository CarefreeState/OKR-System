package com.macaku.medal.handler;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.medal.domain.po.UserMedal;
import com.macaku.medal.service.UserMedalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 12:21
 */
@Component
@Slf4j
public abstract class ApplyMedalHandler {

    private final UserMedalService userMedalService = SpringUtil.getBean(UserMedalService.class);

    private ApplyMedalHandler medalHandler;

    public abstract void handle(Object object);

    public void setNextHandler(ApplyMedalHandler medalHandler) {
        this.medalHandler = medalHandler;
    }

    protected void doNextHandler(Object object) {
        if(Objects.nonNull(medalHandler)) {
            medalHandler.handle(object);
        }
    }

    protected void saveMedalEntry(Long userId, Long medalId, Long credit, Function<Long, Integer> getLevelStrategy) {
        Integer level = getLevelStrategy.apply(credit);
        // 1. 获取用户的徽章
        UserMedal userMedal = userMedalService.lambdaQuery()
                .eq(UserMedal::getUserId, userId)
                .eq(UserMedal::getMedalId, medalId).one();
        if(Objects.isNull(userMedal)) {
            // 插入新的
            UserMedal medal = new UserMedal();
            medal.setMedalId(medalId);
            medal.setCredit(credit);
            medal.setUserId(userId);
            medal.setLevel(level);
            medal.setIssueTime(new Date());
            userMedalService.save(medal);
        } else {
            Integer oldLevel = userMedal.getLevel();
            // 更新积分，判断是否更新等级，如果更新等级则标记为未读（新的一次颁布）
            UserMedal medal = new UserMedal();
            medal.setCredit(credit);
            if(oldLevel.compareTo(level) < 0) {
                medal.setLevel(level);
                medal.setIsRead(Boolean.FALSE);
                medal.setIssueTime(new Date());
            }
            userMedalService.lambdaUpdate()
                    .eq(UserMedal::getUserId, userId)
                    .eq(UserMedal::getMedalId, medalId)
                    .update(medal);
        }
    }

}
