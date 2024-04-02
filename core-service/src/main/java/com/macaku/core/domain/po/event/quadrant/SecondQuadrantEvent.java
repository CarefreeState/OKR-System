package com.macaku.core.domain.po.event.quadrant;

import com.baomidou.mybatisplus.annotation.TableField;
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

    @TableField("core_id")
    private Long coreId; // OKR ID

    @TableField("id")
    private Long id; // 第二象限 ID

    @TableField("cycle")
    private Integer cycle; // 第二象限周期

    @TableField("deadline")
    private Date deadline; // 第二象限截止时间

}
