package com.macaku.core.domain.po.event.quadrant;

import com.baomidou.mybatisplus.annotation.TableField;
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

    private Long coreId; // OKR ID

    private Long id; // 第三象限 ID

    private Integer cycle; // 第三象限周期

    private Date deadline; // 第三象限截止时间

}
