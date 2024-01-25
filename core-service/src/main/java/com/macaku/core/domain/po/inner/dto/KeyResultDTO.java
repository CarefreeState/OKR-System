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
 * Date: 2024-01-21
 * Time: 2:25
 */
@ApiModel("关键结果数据")
@Data
public class KeyResultDTO {

    @ApiModelProperty("第一象限 ID")
    private Long firstQuadrantId;

    @ApiModelProperty("关键结果内容")
    private String content;

    @ApiModelProperty("完成概率")
    private Integer probability;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(firstQuadrantId)) {
            messageBuilder.append("\n-> 第一象限 ID 为 null");
        }
        if(!StringUtils.hasText(content)) {
            messageBuilder.append("\n-> 关键结果没有内容");
        }
        if(Objects.isNull(probability) ||
                probability.compareTo(0) < 0 || probability.compareTo(100) > 0) {
            messageBuilder.append("\n-> 完成概率非法");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
