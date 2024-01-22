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
 * Time: 18:57
 */
@Data
@ApiModel(description = "任务更新实体")
public class TaskUpdateDTO {

    @ApiModelProperty("任务 ID")
    private Long id;

    @ApiModelProperty("任务内容")
    private String content;

    @ApiModelProperty("是否完成")
    private Boolean isCompleted;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(id)) {
            messageBuilder.append("-> 任务 ID 为 null\n");
        }
        if(!StringUtils.hasText(content)) {
            messageBuilder.append("-> 没有内容\n");
        }
        if(Objects.isNull(isCompleted)) {
            messageBuilder.append("-> 任务状态未知\n");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
