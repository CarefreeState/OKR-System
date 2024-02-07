package com.macaku.center.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-02-07
 * Time: 23:16
 */
@ApiModel("用户信息")
@Data
public class UserVO {

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

}