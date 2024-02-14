package com.macaku.center.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-02-03
 * Time: 16:50
 */
@ApiModel("团队成员数据")
@Data
public class TeamMemberVO {

    @TableField("user_id")
    @ApiModelProperty("用户 ID")
    private Long userId;

    @TableField("nickname")
    @ApiModelProperty("昵称")
    private String nickname;

    @TableField("photo")
    @ApiModelProperty("头像")
    private String photo;

    @TableField("email")
    @ApiModelProperty("邮箱")
    private String email;

    @TableField("phone")
    @ApiModelProperty("手机号")
    private String phone;

    @TableField("create_time")
    @ApiModelProperty("加入时间")
    private Date createTime;

    @ApiModelProperty("是否有子团队")
    private Boolean isExtend;

}
