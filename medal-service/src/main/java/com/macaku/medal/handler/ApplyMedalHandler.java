package com.macaku.medal.handler;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.medal.domain.config.MedalMap;
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

    private final MedalMap medalMap = SpringUtil.getBean(MedalMap.class);

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

    protected void saveMedalEntry(Long userId, Long medalId, Long credit, UserMedal dbUserMedal, Function<Long, Integer> getLevelStrategy) {
        if(!medalMap.containsKey(medalId)) {
            return;
        }
        String medalName = medalMap.get(medalId).getName();
        Integer level = getLevelStrategy.apply(credit);
        // 1. 获取用户的徽章
        if(Objects.isNull(dbUserMedal)) {
            // 插入新的
            UserMedal medal = new UserMedal();
            medal.setMedalId(medalId);
            medal.setCredit(credit);
            medal.setUserId(userId);
            medal.setLevel(level);
            if(level > 0) {
                medal.setIssueTime(new Date());
                log.info("颁布勋章 {} {} 等级 {} -> 用户 {} ", medalId, medalName, level, userId);
            }
            userMedalService.save(medal);
        } else {
            Integer oldLevel = dbUserMedal.getLevel();
            // 更新积分，判断是否更新等级，如果更新等级则标记为未读（新的一次颁布）
            UserMedal medal = new UserMedal();
            medal.setCredit(credit);
            // 只升级不降级
            if(oldLevel.compareTo(level) < 0) {
                medal.setLevel(level);
                medal.setIsRead(Boolean.FALSE);
                medal.setIssueTime(new Date());
                log.info("颁布勋章 {} {} 等级 {} -> 用户 {} ", medalId, medalName, level, userId);
            }
            userMedalService.lambdaUpdate()
                    .eq(UserMedal::getUserId, userId)
                    .eq(UserMedal::getMedalId, medalId)
                    .update(medal);
        }
        userMedalService.deleteDbUserMedalCache(userId, medalId);
    }

}
