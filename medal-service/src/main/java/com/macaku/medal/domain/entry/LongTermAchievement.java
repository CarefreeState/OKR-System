package com.macaku.medal.domain.entry;

import lombok.Builder;
import lombok.Getter;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 12:04
 */
@Getter
@Builder
public class LongTermAchievement {

    private Long userId;

    private Boolean isCompleted;

}
