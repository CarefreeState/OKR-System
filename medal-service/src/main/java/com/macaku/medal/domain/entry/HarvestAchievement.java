package com.macaku.medal.domain.entry;

import lombok.Builder;
import lombok.Getter;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 11:59
 */
@Getter
@Builder
public class HarvestAchievement {

    private Long userId;

    private Long degree;

}
