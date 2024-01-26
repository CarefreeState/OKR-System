package com.macaku.core.domain.po.inner;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName priority_number_one
 */
@TableName(value ="priority_number_one")
@ApiModel("Priority 1")
@Data
public class PriorityNumberOne implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "第二象限 ID")
    private Long secondQuadrantId;

    @ApiModelProperty(value = "P1 内容")
    private String content;

    @ApiModelProperty(value = "是否完成")
    private Boolean isCompleted;

    @ApiModelProperty(value = "乐观锁")
    @JsonIgnore
    private Integer version;

    @ApiModelProperty(value = "是否删除")
    @JsonIgnore
    private Boolean isDeleted;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}