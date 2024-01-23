package com.macaku.core.domain.po.quadrant.vo;

import com.macaku.core.domain.po.inner.Action;
import com.macaku.core.domain.po.quadrant.ThirdQuadrant;
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
@ApiModel("第三象限详细信息")
@Data
public class ThirdQuadrantVO extends ThirdQuadrant {

    @ApiModelProperty("行动列表")
    private List<Action> actions;

}
