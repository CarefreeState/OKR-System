package com.macaku.core.domain.po.inner.dto;

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
 * Date: 2024-01-22
 * Time: 1:59
 */
@ApiModel("任务数据")
@Data
public class TaskDTO {

    @ApiModelProperty("象限 ID")
    private Long quadrantId;

    @ApiModelProperty("任务内容")
    private String content;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(quadrantId)) {
            messageBuilder.append("-> 象限 ID 为 null\n");
        }
        if(!StringUtils.hasText(content)) {
            messageBuilder.append("-> 没有内容\n");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }
}
