package com.macaku.center.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName team_personal_okr
 */
@TableName(value ="team_personal_okr")
@ApiModel("团队个人 OKR")
@Data
public class TeamPersonalOkr implements Serializable {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("内核 ID")
    private Long coreId;

    @ApiModelProperty("团队 ID")
    private Long teamId;

    @ApiModelProperty("成员 ID")
    private Long userId;

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