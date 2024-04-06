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
public class FirstQuadrantEvent {

    private Long coreId; // OKR ID

    private Date deadline; // 第一象限截止时间

}
