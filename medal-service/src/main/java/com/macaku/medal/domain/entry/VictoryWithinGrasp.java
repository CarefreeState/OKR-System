package com.macaku.medal.domain.entry;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 12:12
 */
@Getter
@ToString
@Builder
public class VictoryWithinGrasp {

    private Long userId;

    private Integer probability;

    private Integer oldProbability;

}
