package com.macaku.center.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName personal_okr
 */
@TableName(value ="personal_okr")
@ApiModel("个人 OKR")
@Data
public class PersonalOkr implements Serializable {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("内核 ID")
    private Long coreId;

    @ApiModelProperty("用户 ID")
    private Long userId;

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