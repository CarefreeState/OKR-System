package com.macaku.center.domain.dto;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 12:51
 */
@ApiModel("邀请码参数")
@Data
public class QRCodeDTO {

    public final static String TEAM_ID = "teamId";

    @ApiModelProperty("团队 OKR ID")
    private Long teamId;

    @ApiModelProperty("页面")
    private String page;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(teamId)) {
            messageBuilder.append("\n-> 团队 OKR ID 为 null");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }
}
