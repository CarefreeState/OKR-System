package com.macaku.user.domain.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.user.domain.po.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 15:27
 */
@ApiModel("微信小程序登录数据")
@Data
public class WxLoginDTO {

    @ApiModelProperty("code")
    private String code;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(!StringUtils.hasText(code)) {
            messageBuilder.append("\n-> code 为 空");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

    public User transToUser() {
        User user = new User();
        return user;
    }

    public static WxLoginDTO create(Map<?, ?> data) {
        return BeanUtil.mapToBean(data, WxLoginDTO.class, Boolean.FALSE, new CopyOptions());
    }

}
