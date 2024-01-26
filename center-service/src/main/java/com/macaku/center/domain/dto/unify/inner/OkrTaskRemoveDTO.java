package com.macaku.center.domain.dto.unify.inner;

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
 * Date: 2024-01-27
 * Time: 1:30
 */
@ApiModel("删除任务所需数据")
@Data
public class OkrTaskRemoveDTO {

    @ApiModelProperty("场景")
    private String scene;

    @ApiModelProperty("任务 ID")
    private Long id;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(id)) {
            messageBuilder.append("\n-> 任务 ID 为 null");
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
