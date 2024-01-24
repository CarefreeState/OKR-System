package com.macaku.user.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName user
 */
@TableName(value ="user")
@ApiModel("用户")
@Data
public class User implements Serializable {

    public final static String EMAIL_PATTERN = "/^([0-9a-zA-Z_\\.\\-\\u4e00-\\u9fa5])+\\@([0-9a-zA-Z_\\.\\-\\])+\\.([a-zA-Z]{2,8})$/";

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("openid")
    private String openid;

    @ApiModelProperty("unionid")
    private String unionid;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("头像")
    private String photo;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("手机号")
    private String phone;

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