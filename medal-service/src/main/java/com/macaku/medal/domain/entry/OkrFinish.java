package com.macaku.medal.domain.entry;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-19
 * Time: 10:32
 */
@Getter
@ToString
@Builder
public class OkrFinish {

    private Long userId;

    private Integer degree;

    private Boolean isAdvance;

}
