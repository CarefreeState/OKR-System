package com.macaku.core.domain.po.event;

import lombok.Data;

import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-03
 * Time: 0:16
 */
@Data
public class ThirdQuadrantEvent {

    private Long id; // OKR ID

    private Long thirdQuadrantId; // 第三象限 ID

    private Integer thirdQuadrantCycle; // 第三象限周期

    private Date thirdQuadrantDeadline; // 第三象限截止时间

}
