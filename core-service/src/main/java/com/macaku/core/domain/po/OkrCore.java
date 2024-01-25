package com.macaku.core.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName okr_core
 */
@TableName(value ="okr_core")
@ApiModel("OKR 内核")
@Data
public class OkrCore implements Serializable {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("庆祝日（星期）")
    private Integer celebrateDay;

    @ApiModelProperty("第二象限周期（秒）")
    private Integer secondQuadrantCycle;

    @ApiModelProperty("第三象限周期（秒）")
    private Integer thirdQuadrantCycle;

    @ApiModelProperty("是否结束")
    private Boolean isOver;

    @ApiModelProperty("总结")
    private String summary;

    @ApiModelProperty("完成度")
    private Integer degree;

    @ApiModelProperty("乐观锁")
    private Integer version;

    @ApiModelProperty("是否删除")
    private Boolean isDeleted;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}