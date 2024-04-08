package com.macaku.medal.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-08
 * Time: 0:40
 */
@ApiModel("用户勋章")
@Data
public class UserMedalVO {

    @ApiModelProperty("勋章 ID")
    private Long medalId;

    @ApiModelProperty("勋章名称")
    private String name;

    @ApiModelProperty("勋章描述")
    private String description;

    @ApiModelProperty("勋章 URL")
    private String url;

    @ApiModelProperty("勋章等级")
    private Integer level;

    @ApiModelProperty("勋章是否已读")
    private Boolean isRead;

    @ApiModelProperty("勋章颁布时间")
    private Date issueTime;
}
