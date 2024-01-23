package com.macaku.core.domain.po.event;

import lombok.Data;

import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-23
 * Time: 12:05
 */
@Data
public class DeadlineEvent {

    private Long id; // OKR ID

    private Date firstQuadrantDeadline; // 第一象限截止时间

    private Long secondQuadrantId; // 第二象限 ID

    private Integer secondQuadrantCycle; // 第二象限周期

    private Date secondQuadrantDeadline; // 第二象限截止时间

    private Long thirdQuadrantId; // 第三象限 ID

    private Integer thirdQuadrantCycle; // 第三象限周期

    private Date thirdQuadrantDeadline; // 第三象限

}
