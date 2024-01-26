package com.macaku.core.domain.po.inner;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName key_result
 */
@TableName(value ="key_result")
@ApiModel("关键结果")
@Data
public class KeyResult implements Serializable {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("第一象限 ID")
    private Long firstQuadrantId;

    @ApiModelProperty("关键结果内容")
    private String content;

    @ApiModelProperty("完成概率")
    private Integer probability;

    @ApiModelProperty("乐观锁")
    @JsonIgnore
    private Integer version;

    @ApiModelProperty("是否删除")
    @JsonIgnore
    private Boolean isDeleted;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}