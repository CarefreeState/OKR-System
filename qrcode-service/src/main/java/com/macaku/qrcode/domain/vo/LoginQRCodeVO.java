package com.macaku.qrcode.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-21
 * Time: 9:40
 */
@ApiModel("小程序登录码")
@Builder
@Data
public class LoginQRCodeVO {

    @ApiModelProperty("小程序码地址")
    private String path;

    @ApiModelProperty("场景值")
    private String secret;

}
