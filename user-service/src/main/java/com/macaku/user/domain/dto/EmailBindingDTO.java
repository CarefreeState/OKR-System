package com.macaku.user.domain.dto;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.email.component.EmailValidator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-09
 * Time: 14:55
 */
@ApiModel("邮箱绑定数据")
@Data
public class EmailBindingDTO {

    @ApiModelProperty("code")
    private String code;

    @ApiModelProperty("email")
    private String email;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(!StringUtils.hasText(code)) {
            messageBuilder.append("\n-> code 为 空");
        }
        if(!StringUtils.hasText(email) || !EmailValidator.isEmailAccessible(email)) {
            messageBuilder.append("\n-> email 非法");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }
}
