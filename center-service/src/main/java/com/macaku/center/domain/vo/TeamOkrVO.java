package com.macaku.center.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 22:37
 */
@ApiModel("团队 OKR 部分数据")
@Data
public class TeamOkrVO {

    @TableField("id")
    @ApiModelProperty("团队 OKR ID")
    private Long id;

    @JsonInclude
    @TableField("parent_team_id")
    @ApiModelProperty("团队 OKR ID")
    private Long parentTeamId;

    @TableField("core_id")
    @ApiModelProperty("内核 ID")
    private Long coreId;

    @TableField("team_name")
    @ApiModelProperty("团队名")
    private String teamName;

    @TableField("is_over")
    @ApiModelProperty("是否结束")
    private Boolean isOver;

    @TableField("create_time")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @TableField("update_time")
    @ApiModelProperty("更新时间")
    private Date updateTime;
}
