package com.macaku.core.domain.po.inner;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName status_flag
 */
@TableName(value ="status_flag")
@ApiModel("状态指标")
@Data
public class StatusFlag implements Serializable {

    public final static String COLOR_PATTERN = "^#([0-9a-fA-F]{6}|[0-9a-fA-F]{3})$";

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("第四象限 ID")
    private Long fourthQuadrantId;

    @ApiModelProperty("指标内容")
    private String label;

    @ApiModelProperty("颜色")
    private String color;

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