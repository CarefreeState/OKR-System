package com.macaku.core.domain.po.event;

import com.macaku.core.domain.po.event.quadrant.FirstQuadrantEvent;
import com.macaku.core.domain.po.event.quadrant.SecondQuadrantEvent;
import com.macaku.core.domain.po.event.quadrant.ThirdQuadrantEvent;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-23
 * Time: 12:05
 */
@Data
public class DeadlineEvent {

    private FirstQuadrantEvent firstQuadrantEvent;

    private SecondQuadrantEvent secondQuadrantEvent;

    private ThirdQuadrantEvent thirdQuadrantEvent;

}
