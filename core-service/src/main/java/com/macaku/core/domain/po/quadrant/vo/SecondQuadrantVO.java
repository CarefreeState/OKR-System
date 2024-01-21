package com.macaku.core.domain.po.quadrant.vo;

import com.macaku.core.domain.po.inner.PriorityNumberOne;
import com.macaku.core.domain.po.inner.PriorityNumberTwo;
import com.macaku.core.domain.po.quadrant.SecondQuadrant;
import lombok.Data;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 2:32
 */
@Data
public class SecondQuadrantVO extends SecondQuadrant {

    private List<PriorityNumberOne> priorityNumberOnes;

    private List<PriorityNumberTwo> priorityNumberTwos;
}
