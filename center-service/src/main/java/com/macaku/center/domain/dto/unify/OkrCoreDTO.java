package com.macaku.center.domain.dto.unify;

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
 * Time: 21:17
 */
@ApiModel("OKR 内核带场景值的数据")
@Data
public class OkrCoreDTO {

    @ApiModelProperty("场景")
    private String scene;

    @ApiModelProperty("OKR 内核 ID")
    private Long coreId;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(coreId)) {
            messageBuilder.append("\n-> 内核 ID 为 null");
        }
        if(!StringUtils.hasText(scene)) {
            messageBuilder.append("\n-> 缺少场景值");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }
}
