package com.macaku.core.domain.po.event;

import lombok.Data;

import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-03
 * Time: 0:15
 */
@Data
public class SecondQuadrantEvent {

    private Long id; // OKR ID

    private Long secondQuadrantId; // 第二象限 ID

    private Integer secondQuadrantCycle; // 第二象限周期

    private Date secondQuadrantDeadline; // 第二象限截止时间

}
