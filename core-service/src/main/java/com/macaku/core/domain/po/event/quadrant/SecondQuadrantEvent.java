package com.macaku.core.domain.po.event.quadrant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-03
 * Time: 0:15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecondQuadrantEvent {

    private Long coreId; // OKR ID

    private Long id; // 第二象限 ID

    private Integer cycle; // 第二象限周期

    private Date deadline; // 第二象限截止时间

}
