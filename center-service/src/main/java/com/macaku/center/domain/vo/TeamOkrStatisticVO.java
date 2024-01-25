package com.macaku.center.domain.vo;

import com.macaku.center.domain.po.TeamOkr;
import com.macaku.core.domain.po.inner.KeyResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 2:50
 */
@ApiModel("团队 OKR 统计数据")
@Data
public class TeamOkrStatisticVO extends TeamOkr {

    @ApiModelProperty("关键结果列表")
    private List<KeyResult> keyResults;

    @ApiModelProperty("均值")
    private Double average;
}
