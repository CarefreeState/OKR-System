package com.macaku.medal.service;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 23:13
 */
public interface TermAchievementService {

    void issueTermAchievement(Long userId, Boolean isCompleted, Boolean oldCompleted);

}
