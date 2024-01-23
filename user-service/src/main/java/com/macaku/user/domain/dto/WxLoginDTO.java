package com.macaku.user.domain.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.JsonUtil;
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

    @ApiModelProperty("encryptedData")
    private String encryptedData;

    @ApiModelProperty("iv")
    private String iv;

    @ApiModelProperty("rawData")
    private String rawData;

    @ApiModelProperty("signature")
    private String signature;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(!StringUtils.hasText(code)) {
            messageBuilder.append("-> code 为 空\n");
        }
        if(!StringUtils.hasText(code)) {
            messageBuilder.append("-> encryptedData 为 空\n");
        }
        if(!StringUtils.hasText(code)) {
            messageBuilder.append("-> iv 为 空\n");
        }
        if(!StringUtils.hasText(code)) {
            messageBuilder.append("-> rawData 为 空\n");
        }
        if(!StringUtils.hasText(code)) {
            messageBuilder.append("-> signature 为 空\n");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

    public User transToUser() {
        User user = new User();
        Map<String, Object> data = JsonUtil.analyzeJson(this.rawData, Map.class);
        user.setNickname((String) data.get("nickname"));
        user.setPhoto((String) data.get("avatarUrl"));
        return user;
    }

    public static WxLoginDTO create(Map<?, ?> data) {
        return BeanUtil.mapToBean(data, WxLoginDTO.class, false, new CopyOptions());
    }
}
