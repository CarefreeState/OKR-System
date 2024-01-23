package com.macaku.core.domain.po.quadrant.vo;

import com.macaku.core.domain.po.inner.PriorityNumberOne;
import com.macaku.core.domain.po.inner.PriorityNumberTwo;
import com.macaku.core.domain.po.quadrant.SecondQuadrant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 2:32
 */
@ApiModel("第二象限详细信息")
@Data
public class SecondQuadrantVO extends SecondQuadrant {

    @ApiModelProperty("P1 列表")
    private List<PriorityNumberOne> priorityNumberOnes;

    @ApiModelProperty("P2 列表")
    private List<PriorityNumberTwo> priorityNumberTwos;
}
