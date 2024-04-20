package com.macaku.corerecord.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-21
 * Time: 4:24
 */
@ApiModel("日记录数据")
@Data
public class DayRecordVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("日期")
    private Date recordDate;

    @ApiModelProperty("信息指数平均值")
    private Double credit1;

    @ApiModelProperty("第二象限任务完成数")
    private Integer credit2;

    @ApiModelProperty("第三象限任务完成数")
    private Integer credit3;

    @ApiModelProperty("状态指标评估值")
    private Integer credit4;

}
