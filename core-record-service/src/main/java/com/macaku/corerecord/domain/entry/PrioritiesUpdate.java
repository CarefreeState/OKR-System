package com.macaku.corerecord.domain.entry;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-22
 * Time: 16:35
 */
@Getter
@ToString
@Builder
public class PrioritiesUpdate {

    private Long coreId;

    private Boolean isCompleted;

    private Boolean oldCompleted;
}
