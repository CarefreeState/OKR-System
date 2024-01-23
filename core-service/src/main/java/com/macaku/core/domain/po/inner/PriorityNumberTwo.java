package com.macaku.core.domain.po.inner;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName priority_number_two
 */
@TableName(value ="priority_number_two")
@ApiModel("Priority 2")
@Data
public class PriorityNumberTwo implements Serializable {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("第二象限 ID")
    private Long secondQuadrantId;

    @ApiModelProperty("P2 内容")
    private String content;

    @ApiModelProperty("是否完成")
    private Boolean isCompleted;

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