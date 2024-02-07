package com.macaku.user.domain.dto.unify;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.user.domain.dto.EmailLoginDTO;
import com.macaku.user.domain.dto.WxLoginDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-24
 * Time: 11:49
 */
@ApiModel("登录数据")
@Data
public class LoginDTO {

    @ApiModelProperty("邮箱登录数据")
    private EmailLoginDTO emailLoginDTO;

    @ApiModelProperty("微信小程序登录数据")
    private WxLoginDTO wxLoginDTO;

    public void validate() {
        try {
            Field[] fields = LoginDTO.class.getDeclaredFields();
            for(Field field : fields) {
                field.setAccessible(true);
                Object o = field.get(this);
                field.setAccessible(false);
                if(Objects.nonNull(o)) {
                    return;
                }
            }
            throw new GlobalServiceException("没有携带登录数据", GlobalServiceStatusCode.PARAM_IS_BLANK);
        } catch (IllegalAccessException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public EmailLoginDTO createEmailLoginDTO() {
        return this.emailLoginDTO;
    }

    public WxLoginDTO createWxLoginDTO() {
        return this.wxLoginDTO;
    }

}
