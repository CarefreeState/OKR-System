package com.macaku.core.domain.po.quadrant.vo;

import com.macaku.core.domain.po.inner.KeyResult;
import com.macaku.core.domain.po.quadrant.FirstQuadrant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 23:09
 */
@ApiModel("第一象限详情信息")
@Data
public class FirstQuadrantVO extends FirstQuadrant {

    @ApiModelProperty("关键结果列表")
    private List<KeyResult> keyResults;

}
