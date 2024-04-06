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
 * Time: 0:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdQuadrantEvent {

    private Long coreId; // OKR ID

    private Long id; // 第三象限 ID

    private Integer cycle; // 第三象限周期

    private Date deadline; // 第三象限截止时间

}
