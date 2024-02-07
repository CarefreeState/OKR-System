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
 * Date: 2024-01-24
 * Time: 11:54
 */
@ApiModel("邮箱登录数据")
@Data
public class EmailLoginDTO {

    @ApiModelProperty("code")
    private String code;

    @ApiModelProperty("email")
    private String email;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(!StringUtils.hasText(code)) {
            messageBuilder.append("\n-> code 为 空");
        }
        if(!StringUtils.hasText(email) || !email.matches(User.EMAIL_PATTERN)) {
            messageBuilder.append("\n-> email 非法");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

    public User transToUser() {
        User user = new User();
        user.setEmail(this.email);
        return user;
    }

    public static EmailLoginDTO create(Map<?, ?> data) {
        return BeanUtil.mapToBean(data, EmailLoginDTO.class, false, new CopyOptions());
    }

}
