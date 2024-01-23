package com.macaku.core.domain.po.quadrant.vo;

import com.macaku.core.domain.po.inner.StatusFlag;
import com.macaku.core.domain.po.quadrant.FourthQuadrant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 2:31
 */
@ApiModel("第四象限详细信息")
@Data
public class FourthQuadrantVO extends FourthQuadrant {

    @ApiModelProperty("状态指标列表")
    private List<StatusFlag> statusFlags;

}
