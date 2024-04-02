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
public class FirstQuadrantEvent {

    private Long id; // OKR ID

    private Date firstQuadrantDeadline; // 第一象限截止时间

}
