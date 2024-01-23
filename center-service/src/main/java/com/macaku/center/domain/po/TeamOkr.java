package com.macaku.center.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName team_okr
 */
@TableName(value ="team_okr")
@ApiModel("团队 OKR")
@Data
public class TeamOkr implements Serializable {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("内核 ID")
    private Long coreId;

    @ApiModelProperty("父团队 ID")
    private Long parentTeamId;

    @ApiModelProperty("管理者 ID")
    private Long managerId;

    @ApiModelProperty("团队名")
    private String teamName;

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