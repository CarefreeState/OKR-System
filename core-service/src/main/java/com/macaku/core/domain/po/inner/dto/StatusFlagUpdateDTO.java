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
 * Time: 17:33
 */
@ApiModel("状态指标更新数据")
@Data
public class StatusFlagUpdateDTO {


    @ApiModelProperty("指标 ID")
    private Long id;

    @ApiModelProperty("指标内容")
    private String label;

    @ApiModelProperty("颜色")
    private String color;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(id)) {
            messageBuilder.append("-> 指标 ID 为 null\n");
        }
        if(!StringUtils.hasText(label)) {
            messageBuilder.append("-> 指标为空\n");
        }
        if(!StringUtils.hasText(color) || !color.matches("^#([0-9a-fA-F]{6}|[0-9a-fA-F]{3})$")) {
            messageBuilder.append("-> 颜色非法\n");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
