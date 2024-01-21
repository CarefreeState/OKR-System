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
 * Time: 2:23
 */
@Data
@ApiModel("状态指标")
public class StatusFlagDTO {

    @ApiModelProperty("第四象限 ID")
    private Long fourthQuadrantId;

    @ApiModelProperty("指标内容")
    private String label;

    @ApiModelProperty("颜色")
    private String color;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(fourthQuadrantId)) {
            messageBuilder.append("-> 第四象限 ID 为 null\n");
        }
        if(!StringUtils.hasText(label)) {
            messageBuilder.append("-> 指标为空\n");
        }
        if(!StringUtils.hasText(color)) {
            messageBuilder.append("-> 没有颜色\n");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }
}
