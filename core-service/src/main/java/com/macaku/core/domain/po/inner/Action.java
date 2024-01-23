package com.macaku.core.domain.po.inner;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName action
 */
@TableName(value ="action")
@ApiModel("具体行动")
@Data
public class Action implements Serializable {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("第三象限 ID")
    private Long thirdQuadrantId;

    @ApiModelProperty("行动内容")
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