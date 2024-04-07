package com.macaku.medal.domain.entry;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 11:59
 */
@Getter
@ToString
@Builder
public class HarvestAchievement {

    private Long userId;

    private Long degree;

}
