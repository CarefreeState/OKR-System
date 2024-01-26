package com.macaku.center.domain.dto.unify.inner;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.domain.po.inner.dto.KeyResultUpdateDTO;
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
 * Time: 0:41
 */
@ApiModel("更新关键结果所需数据")
@Data
public class OkrKeyResultUpdateDTO {

    @ApiModelProperty("场景")
    private String scene;

    @ApiModelProperty("更新关键结果的数据")
    private KeyResultUpdateDTO keyResultUpdateDTO;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(keyResultUpdateDTO)) {
            messageBuilder.append("\n-> 更新关键结果的数据 为 null");
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
