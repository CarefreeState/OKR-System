package com.macaku.center.domain.dto.unify;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 19:18
 */
@ApiModel("OKR 操作数据")
@Data
public class OkrOperateDTO {

    @ApiModelProperty("场景")
    private String scene;

    @ApiModelProperty("团队 OKR ID")
    private Long teamOkrId;

    @ApiModelProperty("邀请密钥")
    private String secret;

    @ApiModelProperty("邀请码类型")
    private String type;

    @ApiModelProperty("团队名")
    private String teamName;


    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(!StringUtils.hasText(scene)) {
            messageBuilder.append("\n-> 缺少场景值");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
