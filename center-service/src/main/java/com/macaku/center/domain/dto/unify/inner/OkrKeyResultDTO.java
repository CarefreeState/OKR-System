package com.macaku.center.domain.dto.unify.inner;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.domain.po.inner.dto.KeyResultDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-27
 * Time: 0:31
 */
@ApiModel("添加关键结果所需数据")
@Data
public class OkrKeyResultDTO {

    @ApiModelProperty("场景")
    private String scene;

    @ApiModelProperty("关键结果数据")
    private KeyResultDTO keyResultDTO;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(keyResultDTO)) {
            messageBuilder.append("\n-> 关键结果 为 null");
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
