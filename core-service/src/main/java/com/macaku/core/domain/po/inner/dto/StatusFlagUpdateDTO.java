package com.macaku.core.domain.po.inner.dto;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.domain.po.inner.StatusFlag;
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
            messageBuilder.append("\n-> 指标 ID 为 null");
        }
        if(!StringUtils.hasText(label)) {
            messageBuilder.append("\n-> 指标为空");
        }
        if(!StringUtils.hasText(color) || !color.matches(StatusFlag.COLOR_PATTERN)) {
            messageBuilder.append("\n-> 颜色非法");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
